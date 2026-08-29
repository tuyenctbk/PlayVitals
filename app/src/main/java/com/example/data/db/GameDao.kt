package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.GameItem
import com.example.data.model.GameSession
import com.example.data.model.ReflexScore
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    // Games
    @Query("SELECT * FROM games WHERE isInLauncher = 1 ORDER BY lastPlayedTimestamp DESC, title ASC")
    fun getLauncherGames(): Flow<List<GameItem>>

    @Query("SELECT * FROM games ORDER BY title ASC")
    fun getAllInstalledGames(): Flow<List<GameItem>>

    @Query("SELECT * FROM games WHERE packageName = :packageName LIMIT 1")
    suspend fun getGameByPackage(packageName: String): GameItem?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGames(games: List<GameItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateGame(game: GameItem)

    @Update
    suspend fun updateGame(game: GameItem)

    @Query("UPDATE games SET isInLauncher = :isInLauncher WHERE packageName = :packageName")
    suspend fun setGameInLauncher(packageName: String, isInLauncher: Boolean)

    @Query("UPDATE games SET isHudEnabled = :isHudEnabled WHERE packageName = :packageName")
    suspend fun setGameHudEnabled(packageName: String, isHudEnabled: Boolean)

    @Query("DELETE FROM games WHERE packageName = :packageName")
    suspend fun deleteGame(packageName: String)

    // Sessions
    @Query("SELECT * FROM sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<GameSession>>

    @Query("SELECT * FROM sessions WHERE packageName = :packageName ORDER BY startTime DESC")
    fun getSessionsForGame(packageName: String): Flow<List<GameSession>>

    @Query("SELECT * FROM sessions WHERE id = :sessionId LIMIT 1")
    suspend fun getSessionById(sessionId: Long): GameSession?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: GameSession): Long

    // Reflex Scores
    @Query("SELECT * FROM reflex_scores WHERE drillType = :drillType ORDER BY scoreValueMs ASC LIMIT 10")
    fun getTopScoresForDrill(drillType: String): Flow<List<ReflexScore>>

    @Query("SELECT MIN(scoreValueMs) FROM reflex_scores WHERE drillType = 'REACTION_SPEED'")
    fun getBestReactionScore(): Flow<Long?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReflexScore(score: ReflexScore)
}
