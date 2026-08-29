package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameItem(
    @PrimaryKey val packageName: String,
    val title: String,
    val customName: String? = null,
    val isAutoDetected: Boolean = false,
    val isInLauncher: Boolean = true,
    val iconPresetIndex: Int = 0,
    val totalPlaytimeMillis: Long = 0L,
    val sessionCount: Int = 0,
    val lastPlayedTimestamp: Long = 0L,
    val minRamRecorded: Int = 0,
    val peakTempRecorded: Float = 0f,
    val avgLatencyRecorded: Int = 0,
    val screenRefreshRate: Int = 60,
    val isHudEnabled: Boolean = true,
    val tagsCsv: String = ""
) {
    val displayName: String
        get() = customName?.takeIf { it.isNotBlank() } ?: title

    val totalPlaytimeMinutes: Long
        get() = totalPlaytimeMillis / (1000L * 60L)

    val isFavorite: Boolean
        get() = sessionCount > 0 || totalPlaytimeMillis > 0

    val tagList: List<String>
        get() = tagsCsv.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
}
