package com.quoren.app.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "memorization_progress")
data class MemorizationProgressEntity(
    @PrimaryKey val ayahId: Int,
    val status: String,
    val scheduleStage: Int,
    val dueEpochDay: Long,
    val lastReviewedEpochDay: Long?,
    val reviewCount: Int,
    val consecutiveHardRatings: Int,
    val isWeak: Boolean,
)

@Entity(tableName = "memorization_session", indices = [Index("lastOpenedAt")])
data class MemorizationSessionEntity(
    @PrimaryKey val sessionId: String,
    val surahId: Int,
    val startAyah: Int,
    val endAyah: Int,
    val reciterId: String,
    val repeatCount: String,
    val mode: String,
    val currentAyah: Int,
    val createdAt: Long,
    val lastOpenedAt: Long,
    val completedAt: Long?,
)

@Entity(tableName = "review_history", indices = [Index("ayahId"), Index("reviewedAt")])
data class ReviewHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ayahId: Int,
    val sessionId: String?,
    val difficulty: String,
    val reviewedAt: Long,
    val dueEpochDay: Long,
)

@Entity(tableName = "daily_goal")
data class DailyGoalEntity(
    @PrimaryKey val id: Int = 0,
    val ayahTarget: Int,
    val completedAyahs: Int,
    val epochDay: Long,
)

@Entity(tableName = "daily_goal_completion", primaryKeys = ["epochDay", "ayahId"])
data class DailyGoalCompletionEntity(val epochDay: Long, val ayahId: Int)

@Dao
interface MemorizationDao {
    @Query("SELECT * FROM memorization_progress ORDER BY ayahId")
    suspend fun exportProgress(): List<MemorizationProgressEntity>

    @Query("SELECT * FROM memorization_session ORDER BY lastOpenedAt DESC")
    suspend fun exportSessions(): List<MemorizationSessionEntity>

    @Query("SELECT * FROM review_history ORDER BY reviewedAt DESC")
    suspend fun exportReviews(): List<ReviewHistoryEntity>

    @Query("SELECT * FROM daily_goal WHERE id = 0")
    suspend fun exportDailyGoal(): DailyGoalEntity?

    @Query("SELECT * FROM daily_goal_completion ORDER BY epochDay, ayahId")
    suspend fun exportGoalCompletions(): List<DailyGoalCompletionEntity>

    @Query("DELETE FROM memorization_progress")
    suspend fun clearProgressForRestore()

    @Query("DELETE FROM memorization_session")
    suspend fun clearSessionsForRestore()

    @Query("DELETE FROM review_history")
    suspend fun clearReviewsForRestore()

    @Query("DELETE FROM daily_goal")
    suspend fun clearDailyGoalForRestore()

    @Query("DELETE FROM daily_goal_completion")
    suspend fun clearGoalCompletionsForRestore()

    @Query("SELECT * FROM memorization_progress ORDER BY ayahId")
    fun observeProgress(): Flow<List<MemorizationProgressEntity>>

    @Query("SELECT * FROM memorization_progress WHERE ayahId = :ayahId")
    fun observeProgress(ayahId: Int): Flow<MemorizationProgressEntity?>

    @Query("SELECT * FROM memorization_progress WHERE ayahId = :ayahId")
    suspend fun getProgress(ayahId: Int): MemorizationProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(item: MemorizationProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun restoreProgress(items: List<MemorizationProgressEntity>)

    @Query("SELECT * FROM memorization_session ORDER BY lastOpenedAt DESC")
    fun observeSessions(): Flow<List<MemorizationSessionEntity>>

    @Query("SELECT * FROM memorization_session WHERE sessionId = :sessionId")
    suspend fun getSession(sessionId: String): MemorizationSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSession(item: MemorizationSessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun restoreSessions(items: List<MemorizationSessionEntity>)

    @Query("DELETE FROM memorization_session WHERE sessionId = :sessionId")
    suspend fun deleteSession(sessionId: String)

    @Query("SELECT * FROM review_history ORDER BY reviewedAt DESC LIMIT :limit")
    fun observeReviewHistory(limit: Int): Flow<List<ReviewHistoryEntity>>

    @Insert
    suspend fun insertReview(item: ReviewHistoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun restoreReviews(items: List<ReviewHistoryEntity>)

    @Query("SELECT * FROM daily_goal WHERE id = 0")
    fun observeDailyGoal(): Flow<DailyGoalEntity?>

    @Query("SELECT * FROM daily_goal WHERE id = 0")
    suspend fun getDailyGoal(): DailyGoalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDailyGoal(item: DailyGoalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun restoreDailyGoal(item: DailyGoalEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCompletion(item: DailyGoalCompletionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun restoreGoalCompletions(items: List<DailyGoalCompletionEntity>)

    @Query("SELECT COUNT(*) FROM daily_goal_completion WHERE epochDay = :epochDay")
    suspend fun countCompletions(epochDay: Long): Int

    @Transaction
    suspend fun recordCompletion(ayahId: Int, epochDay: Long) {
        val existing = getDailyGoal()
        val target = if (existing == null || existing.epochDay != epochDay) existing?.ayahTarget ?: DEFAULT_DAILY_GOAL else existing.ayahTarget
        insertCompletion(DailyGoalCompletionEntity(epochDay, ayahId))
        upsertDailyGoal(DailyGoalEntity(ayahTarget = target, completedAyahs = countCompletions(epochDay), epochDay = epochDay))
    }

    companion object {
        const val DEFAULT_DAILY_GOAL = 3
    }
}
