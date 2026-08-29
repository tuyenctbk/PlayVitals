package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class GameSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val packageName: String,
    val gameTitle: String,
    val startTime: Long,
    val durationMillis: Long,
    val avgLatencyMs: Int,
    val pingJitterMs: Int,
    val minFreeRamPercent: Int,
    val peakBatteryTempC: Float,
    val overallScore: Int,
    val pingScore: Int,
    val memoryScore: Int,
    val tempScore: Int,
    val summaryText: String
)
