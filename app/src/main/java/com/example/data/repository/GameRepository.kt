package com.example.data.repository

import com.example.data.db.GameDao
import com.example.data.model.GameItem
import com.example.data.model.GameSession
import com.example.data.model.ReflexScore
import kotlinx.coroutines.flow.Flow

class GameRepository(private val dao: GameDao) {
    val launcherGames: Flow<List<GameItem>> = dao.getLauncherGames()
    val allGames: Flow<List<GameItem>> = dao.getAllInstalledGames()
    val allSessions: Flow<List<GameSession>> = dao.getAllSessions()
    val bestReactionScore: Flow<Long?> = dao.getBestReactionScore()

    fun getSessionsForGame(packageName: String): Flow<List<GameSession>> {
        return dao.getSessionsForGame(packageName)
    }

    suspend fun getGame(packageName: String): GameItem? {
        return dao.getGameByPackage(packageName)
    }

    suspend fun getSession(sessionId: Long): GameSession? {
        return dao.getSessionById(sessionId)
    }

    suspend fun insertGames(games: List<GameItem>) {
        dao.insertGames(games)
    }

    suspend fun insertOrUpdateGame(game: GameItem) {
        dao.insertOrUpdateGame(game)
    }

    suspend fun setGameInLauncher(packageName: String, isInLauncher: Boolean) {
        dao.setGameInLauncher(packageName, isInLauncher)
    }

    suspend fun setGameHudEnabled(packageName: String, isHudEnabled: Boolean) {
        dao.setGameHudEnabled(packageName, isHudEnabled)
    }

    suspend fun deleteGame(packageName: String) {
        dao.deleteGame(packageName)
    }

    suspend fun saveSession(session: GameSession): Long {
        val sessionId = dao.insertSession(session)
        // Update game aggregations
        val existingGame = dao.getGameByPackage(session.packageName)
        if (existingGame != null) {
            val updated = existingGame.copy(
                totalPlaytimeMillis = existingGame.totalPlaytimeMillis + session.durationMillis,
                sessionCount = existingGame.sessionCount + 1,
                lastPlayedTimestamp = session.startTime + session.durationMillis,
                minRamRecorded = if (existingGame.minRamRecorded == 0) session.minFreeRamPercent else minOf(existingGame.minRamRecorded, session.minFreeRamPercent),
                peakTempRecorded = maxOf(existingGame.peakTempRecorded, session.peakBatteryTempC),
                avgLatencyRecorded = if (existingGame.avgLatencyRecorded == 0) session.avgLatencyMs else (existingGame.avgLatencyRecorded + session.avgLatencyMs) / 2
            )
            dao.updateGame(updated)
        }
        return sessionId
    }

    fun getTopScores(drillType: String): Flow<List<ReflexScore>> = dao.getTopScoresForDrill(drillType)

    suspend fun saveReflexScore(score: ReflexScore) {
        dao.insertReflexScore(score)
    }
}
