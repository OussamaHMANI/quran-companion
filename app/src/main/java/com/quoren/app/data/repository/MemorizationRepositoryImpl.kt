package com.quoren.app.data.repository

import androidx.room.withTransaction
import com.quoren.app.data.local.DailyGoalEntity
import com.quoren.app.data.local.MemorizationDao
import com.quoren.app.data.local.MemorizationProgressEntity
import com.quoren.app.data.local.MemorizationSessionEntity
import com.quoren.app.data.local.QuranDatabase
import com.quoren.app.data.local.ReviewHistoryEntity
import com.quoren.app.domain.memorization.AyahReviewState
import com.quoren.app.domain.memorization.MemorizationStatus
import com.quoren.app.domain.memorization.ReviewDifficulty
import com.quoren.app.domain.memorization.SpacedRepetitionEngine
import com.quoren.app.domain.model.DailyGoal
import com.quoren.app.domain.model.DifficultyRating
import com.quoren.app.domain.model.MemorizationCounts
import com.quoren.app.domain.model.MemorizationMode
import com.quoren.app.domain.model.MemorizationProgress
import com.quoren.app.domain.model.MemorizationSession
import com.quoren.app.domain.model.RepeatCount
import com.quoren.app.domain.model.ReviewRecord
import com.quoren.app.domain.repository.MemorizationRepository
import com.quoren.app.domain.repository.QuranRepository
import com.quoren.app.domain.repository.ReciterRepository
import java.time.LocalDate
import java.time.Instant
import java.time.ZoneOffset
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

@Singleton
class MemorizationRepositoryImpl @Inject constructor(
    private val db: QuranDatabase,
    private val planner: SpacedRepetitionEngine,
    private val quran: QuranRepository,
    private val reciters: ReciterRepository,
) : MemorizationRepository {

    private val dao: MemorizationDao = db.memorizationDao()

    override fun observeProgress(): Flow<List<MemorizationProgress>> = dao.observeProgress().map { rows -> rows.map { it.toDomain() } }

    override fun observeProgress(ayahId: Int): Flow<MemorizationProgress?> = dao.observeProgress(ayahId).map { it?.toDomain() }

    override suspend fun setStatus(ayahId: Int, status: MemorizationStatus) {
        require(quran.getAyah(ayahId) != null) { "Unknown ayah" }
        val today = LocalDate.now()
        val current = dao.getProgress(ayahId)?.toState() ?: planner.newAyah(ayahId, today)
        val updated = current.copy(
            status = status,
            scheduleStage = if (status == MemorizationStatus.MEMORIZED) SpacedRepetitionEngine.INTERVAL_DAYS.lastIndex else current.scheduleStage,
            dueDate = if (status == MemorizationStatus.MEMORIZED) today.plusDays(90) else current.dueDate,
        )
        dao.upsertProgress(updated.toEntity())
    }

    override suspend fun recordReview(
        ayahId: Int,
        rating: DifficultyRating,
        reviewedAt: LocalDate,
        sessionId: String?,
    ): MemorizationProgress {
        require(quran.getAyah(ayahId) != null) { "Unknown ayah" }
        val current = dao.getProgress(ayahId)?.toState() ?: planner.newAyah(ayahId, reviewedAt)
        val difficulty = when (rating) {
            DifficultyRating.HARD -> ReviewDifficulty.HARD
            DifficultyRating.MEDIUM -> ReviewDifficulty.MEDIUM
            DifficultyRating.EASY -> ReviewDifficulty.EASY
        }
        val outcome = planner.recordReview(current, difficulty, reviewedAt)
        db.withTransaction {
            dao.upsertProgress(outcome.state.toEntity())
            dao.insertReview(
                ReviewHistoryEntity(
                    ayahId = ayahId,
                    sessionId = sessionId,
                    difficulty = rating.name,
                    reviewedAt = reviewedAt.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
                    dueEpochDay = outcome.state.dueDate.toEpochDay(),
                ),
            )
        }
        return outcome.state.toDomain()
    }

    override fun observeSessions(): Flow<List<MemorizationSession>> =
        dao.observeSessions().map { rows -> rows.map { it.toDomain() } }

    override suspend fun createSession(
        surahId: Int,
        startAyah: Int,
        endAyah: Int,
        reciterId: String,
        repeatCount: RepeatCount,
        mode: MemorizationMode,
    ): MemorizationSession {
        val surah = quran.getSurahs().firstOrNull { it.id == surahId } ?: error("Unknown surah")
        require(startAyah in 1..surah.numberOfAyahs && endAyah in startAyah..surah.numberOfAyahs) { "Invalid ayah range" }
        val reciter = reciters.getReciterById(reciterId) ?: error("Unknown reciter")
        val now = System.currentTimeMillis()
        val entity = MemorizationSessionEntity(
            sessionId = UUID.randomUUID().toString(),
            surahId = surahId,
            startAyah = startAyah,
            endAyah = endAyah,
            reciterId = reciter.id,
            repeatCount = repeatCount.name,
            mode = mode.name,
            currentAyah = startAyah,
            createdAt = now,
            lastOpenedAt = now,
            completedAt = null,
        )
        dao.upsertSession(entity)
        return entity.toDomain()
    }

    override suspend fun updateSessionPosition(sessionId: String, ayahNumber: Int) {
        val current = dao.getSession(sessionId) ?: return
        require(ayahNumber in current.startAyah..current.endAyah) { "Ayah is outside the session range" }
        dao.upsertSession(current.copy(currentAyah = ayahNumber, lastOpenedAt = System.currentTimeMillis()))
    }

    override suspend fun completeSession(sessionId: String) {
        val session = dao.getSession(sessionId) ?: return
        dao.upsertSession(session.copy(completedAt = System.currentTimeMillis(), lastOpenedAt = System.currentTimeMillis()))
    }

    override suspend fun getSession(sessionId: String): MemorizationSession? = dao.getSession(sessionId)?.toDomain()

    override suspend fun deleteSession(sessionId: String) = dao.deleteSession(sessionId)

    override fun observeReviewHistory(limit: Int): Flow<List<ReviewRecord>> =
        dao.observeReviewHistory(limit.coerceIn(1, 500)).map { rows -> rows.map { it.toDomain() } }

    override fun observeCounts(today: LocalDate): Flow<MemorizationCounts> =
        combine(dao.observeProgress(), dao.observeReviewHistory(500), dao.observeSessions()) { progress, reviews, sessions ->
            MemorizationCounts(
                newCount = progress.count { it.status == MemorizationStatus.NEW.name },
                learningCount = progress.count { it.status == MemorizationStatus.LEARNING.name },
                reviewCount = progress.count { it.status == MemorizationStatus.REVIEW.name },
                memorizedCount = progress.count { it.status == MemorizationStatus.MEMORIZED.name },
                dueCount = progress.count { it.status != MemorizationStatus.NEW.name && it.dueEpochDay <= today.toEpochDay() },
                weakCount = progress.count { it.isWeak },
                totalReviews = progress.sumOf { it.reviewCount },
                completedSessions = sessions.count { it.completedAt != null },
                studiedMinutes = 0,
            )
        }

    override fun observeDailyGoal(): Flow<DailyGoal> = dao.observeDailyGoal().map { entity ->
        val today = LocalDate.now()
        if (entity == null || entity.epochDay != today.toEpochDay()) {
            DailyGoal(entity?.ayahTarget ?: MemorizationDao.DEFAULT_DAILY_GOAL, 0, today)
        } else {
            DailyGoal(entity.ayahTarget, entity.completedAyahs, today)
        }
    }

    override suspend fun setDailyGoal(ayahTarget: Int) {
        require(ayahTarget in SUPPORTED_GOALS) { "Unsupported daily goal" }
        val today = LocalDate.now()
        dao.upsertDailyGoal(
            DailyGoalEntity(
                ayahTarget = ayahTarget,
                completedAyahs = dao.countCompletions(today.toEpochDay()),
                epochDay = today.toEpochDay(),
            ),
        )
    }

    override suspend fun completeAyahForToday(ayahId: Int, today: LocalDate) {
        require(quran.getAyah(ayahId) != null) { "Unknown ayah" }
        dao.recordCompletion(ayahId, today.toEpochDay())
    }

    private fun MemorizationProgressEntity.toState() = AyahReviewState(
        ayahId = ayahId,
        status = MemorizationStatus.valueOf(status),
        scheduleStage = scheduleStage,
        dueDate = LocalDate.ofEpochDay(dueEpochDay),
        lastReviewedDate = lastReviewedEpochDay?.let(LocalDate::ofEpochDay),
        reviewCount = reviewCount,
        consecutiveHardRatings = consecutiveHardRatings,
        isWeak = isWeak,
    )

    private fun AyahReviewState.toEntity() = MemorizationProgressEntity(
        ayahId = ayahId,
        status = status.name,
        scheduleStage = scheduleStage,
        dueEpochDay = dueDate.toEpochDay(),
        lastReviewedEpochDay = lastReviewedDate?.toEpochDay(),
        reviewCount = reviewCount,
        consecutiveHardRatings = consecutiveHardRatings,
        isWeak = isWeak,
    )

    private fun MemorizationProgressEntity.toDomain() = toState().toDomain()

    private fun AyahReviewState.toDomain() = MemorizationProgress(
        ayahId = ayahId,
        status = status,
        scheduleStage = scheduleStage,
        dueDate = dueDate,
        lastReviewedDate = lastReviewedDate,
        reviewCount = reviewCount,
        consecutiveHardRatings = consecutiveHardRatings,
        isWeak = isWeak,
    )

    private fun MemorizationSessionEntity.toDomain() = MemorizationSession(
        sessionId = sessionId,
        surahId = surahId,
        startAyah = startAyah,
        endAyah = endAyah,
        reciterId = reciterId,
        repeatCount = RepeatCount.valueOf(repeatCount),
        mode = MemorizationMode.valueOf(mode),
        currentAyah = currentAyah,
        createdAt = createdAt,
        lastOpenedAt = lastOpenedAt,
        completedAt = completedAt,
    )

    private fun ReviewHistoryEntity.toDomain() = ReviewRecord(
        id = id,
        ayahId = ayahId,
        sessionId = sessionId,
        difficulty = DifficultyRating.valueOf(difficulty),
        reviewedAt = Instant.ofEpochMilli(reviewedAt).atZone(ZoneOffset.UTC).toLocalDate().toEpochDay(),
        dueDate = LocalDate.ofEpochDay(dueEpochDay),
    )

    private companion object {
        val SUPPORTED_GOALS = setOf(1, 3, 5, 10)
    }
}
