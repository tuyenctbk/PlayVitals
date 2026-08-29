package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reflex_scores")
data class ReflexScore(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val drillType: String, // "REACTION_SPEED" or "TARGET_BLITZ"
    val scoreValueMs: Long,
    val accuracyPercent: Float = 100f,
    val timestamp: Long = System.currentTimeMillis(),
    val rankBadge: String = "Pro"
)
