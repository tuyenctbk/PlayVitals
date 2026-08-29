package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.model.GameItem
import com.example.data.model.GameSession
import com.example.data.model.HudSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class FirestoreSyncRepository(private val context: Context) {

    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    val currentUserId: String?
        get() = auth.currentUser?.uid

    val currentUserEmail: String?
        get() = auth.currentUser?.email

    val isSignedIn: Boolean
        get() = auth.currentUser != null

    suspend fun syncSessionToCloud(session: GameSession): Boolean {
        val uid = currentUserId ?: return false
        return try {
            val sessionData = hashMapOf(
                "sessionId" to session.id,
                "packageName" to session.packageName,
                "gameTitle" to session.gameTitle,
                "startTime" to session.startTime,
                "durationMillis" to session.durationMillis,
                "avgLatencyMs" to session.avgLatencyMs,
                "minFreeRamPercent" to session.minFreeRamPercent,
                "peakBatteryTempC" to session.peakBatteryTempC,
                "overallScore" to session.overallScore,
                "pingScore" to session.pingScore,
                "memoryScore" to session.memoryScore,
                "tempScore" to session.tempScore,
                "summaryText" to session.summaryText,
                "syncedAt" to System.currentTimeMillis()
            )

            val docId = if (session.id != 0L) session.id.toString() else session.startTime.toString()
            firestore.collection("users")
                .document(uid)
                .collection("game_sessions")
                .document(docId)
                .set(sessionData, SetOptions.merge())
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreSync", "Failed to sync session: ${e.message}")
            false
        }
    }

    suspend fun syncSettingsToCloud(settings: HudSettings): Boolean {
        val uid = currentUserId ?: return false
        return try {
            val settingsData = hashMapOf(
                "isHudMasterEnabled" to settings.isHudMasterEnabled,
                "visualStyle" to settings.visualStyle.name,
                "themeMode" to settings.themeMode.name,
                "showFreeRam" to settings.showFreeRam,
                "showBatteryTemp" to settings.showBatteryTemp,
                "showBatteryLevel" to settings.showBatteryLevel,
                "showNetworkLatency" to settings.showNetworkLatency,
                "showScreenRefreshRate" to settings.showScreenRefreshRate,
                "backgroundOpacity" to settings.backgroundOpacity,
                "textSizeScale" to settings.textSizeScale,
                "warnLowRamThreshold" to settings.warnLowRamThreshold,
                "warnHighTempThreshold" to settings.warnHighTempThreshold,
                "isAutoRefreshRateLimiterEnabled" to settings.isAutoRefreshRateLimiterEnabled,
                "lowBatteryRefreshRateThreshold" to settings.lowBatteryRefreshRateThreshold,
                "limitedRefreshRateHz" to settings.limitedRefreshRateHz,
                "hudPositionX" to settings.hudPositionX,
                "hudPositionY" to settings.hudPositionY,
                "syncedAt" to System.currentTimeMillis()
            )

            firestore.collection("users")
                .document(uid)
                .collection("settings")
                .document("hud_preferences")
                .set(settingsData, SetOptions.merge())
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreSync", "Failed to sync settings: ${e.message}")
            false
        }
    }

    suspend fun syncFullBackupToCloud(sessions: List<GameSession>, games: List<GameItem>): Boolean {
        val uid = currentUserId ?: return false
        return try {
            val sessionsList = sessions.map { s ->
                hashMapOf(
                    "id" to s.id,
                    "packageName" to s.packageName,
                    "gameTitle" to s.gameTitle,
                    "startTime" to s.startTime,
                    "durationMillis" to s.durationMillis,
                    "avgLatencyMs" to s.avgLatencyMs,
                    "pingJitterMs" to s.pingJitterMs,
                    "minFreeRamPercent" to s.minFreeRamPercent,
                    "peakBatteryTempC" to s.peakBatteryTempC,
                    "overallScore" to s.overallScore,
                    "pingScore" to s.pingScore,
                    "memoryScore" to s.memoryScore,
                    "tempScore" to s.tempScore,
                    "summaryText" to s.summaryText
                )
            }

            val gameTagsMap = games.associate { game: GameItem -> game.packageName to game.tagsCsv }

            val backupPayload = hashMapOf(
                "backupTimestamp" to System.currentTimeMillis(),
                "sessionCount" to sessions.size,
                "gameTags" to gameTagsMap,
                "sessions" to sessionsList
            )

            firestore.collection("users")
                .document(uid)
                .collection("backups")
                .document("room_database_backup")
                .set(backupPayload)
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreSync", "Failed to perform full cloud backup: ${e.message}")
            false
        }
    }

    suspend fun restoreFullBackupFromCloud(): Pair<List<GameSession>, Map<String, String>>? {
        val uid = currentUserId ?: return null
        return try {
            val doc = firestore.collection("users")
                .document(uid)
                .collection("backups")
                .document("room_database_backup")
                .get()
                .await()

            if (!doc.exists()) return null

            @Suppress("UNCHECKED_CAST")
            val tagsMapRaw = doc.get("gameTags") as? Map<String, String> ?: emptyMap()

            @Suppress("UNCHECKED_CAST")
            val sessionsRaw = doc.get("sessions") as? List<Map<String, Any>> ?: emptyList()

            val restoredSessions = sessionsRaw.mapNotNull { map ->
                val pkg = map["packageName"] as? String ?: return@mapNotNull null
                val title = map["gameTitle"] as? String ?: "Game"
                val startTime = (map["startTime"] as? Number)?.toLong() ?: System.currentTimeMillis()
                val duration = (map["durationMillis"] as? Number)?.toLong() ?: 60000L
                val overall = ((map["overallScore"] as? Number)?.toInt()) ?: 80

                GameSession(
                    id = (map["id"] as? Number)?.toLong() ?: 0L,
                    packageName = pkg,
                    gameTitle = title,
                    startTime = startTime,
                    durationMillis = duration,
                    avgLatencyMs = ((map["avgLatencyMs"] as? Number)?.toInt()) ?: 35,
                    pingJitterMs = ((map["pingJitterMs"] as? Number)?.toInt()) ?: 8,
                    minFreeRamPercent = ((map["minFreeRamPercent"] as? Number)?.toInt()) ?: 50,
                    peakBatteryTempC = ((map["peakBatteryTempC"] as? Number)?.toFloat()) ?: 28.0f,
                    overallScore = overall,
                    pingScore = ((map["pingScore"] as? Number)?.toInt()) ?: 80,
                    memoryScore = ((map["memoryScore"] as? Number)?.toInt()) ?: 80,
                    tempScore = ((map["tempScore"] as? Number)?.toInt()) ?: 80,
                    summaryText = map["summaryText"] as? String ?: "Restored session."
                )
            }

            Pair(restoredSessions, tagsMapRaw)
        } catch (e: Exception) {
            Log.e("FirestoreSync", "Failed to restore backup from cloud: ${e.message}")
            null
        }
    }

    suspend fun fetchCloudSessions(): List<GameSession> {
        val uid = currentUserId ?: return emptyList()
        return try {
            val snapshot = firestore.collection("users")
                .document(uid)
                .collection("game_sessions")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val pkg = doc.getString("packageName") ?: return@mapNotNull null
                val title = doc.getString("gameTitle") ?: "Game"
                val startTime = doc.getLong("startTime") ?: System.currentTimeMillis()
                val duration = doc.getLong("durationMillis") ?: 60000L
                val overall = (doc.getLong("overallScore") ?: 80L).toInt()
                val sessionDocId = doc.id.toLongOrNull() ?: 0L

                GameSession(
                    id = sessionDocId,
                    packageName = pkg,
                    gameTitle = title,
                    startTime = startTime,
                    durationMillis = duration,
                    avgLatencyMs = (doc.getLong("avgLatencyMs") ?: 35L).toInt(),
                    pingJitterMs = 8,
                    minFreeRamPercent = (doc.getLong("minFreeRamPercent") ?: 50L).toInt(),
                    peakBatteryTempC = (doc.getDouble("peakBatteryTempC") ?: 28.0).toFloat(),
                    overallScore = overall,
                    pingScore = (doc.getLong("pingScore") ?: 80L).toInt(),
                    memoryScore = (doc.getLong("memoryScore") ?: 80L).toInt(),
                    tempScore = (doc.getLong("tempScore") ?: 80L).toInt(),
                    summaryText = doc.getString("summaryText") ?: "Synced session record."
                )
            }
        } catch (e: Exception) {
            Log.e("FirestoreSync", "Failed to fetch sessions: ${e.message}")
            emptyList()
        }
    }
}
