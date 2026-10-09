package com.quoren.app.domain.usecase

import com.quoren.app.domain.memorization.MemorizationStatus
import com.quoren.app.domain.model.DailyGoal
import com.quoren.app.domain.model.DifficultyRating
import com.quoren.app.domain.model.MemorizationCounts
import com.quoren.app.domain.model.MemorizationMode
import com.quoren.app.domain.model.MemorizationProgress
import com.quoren.app.domain.model.MemorizationSession
import com.quoren.app.domain.model.RepeatCount
import com.quoren.app.domain.model.ReviewRecord
import com.quoren.app.domain.repository.MemorizationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class ObserveMemorizationProgressUseCase @Inject constructor(private val repository: MemorizationRepository) {
    operator fun invoke(): Flow<List<MemorizationProgress>> = repository.observeProgress()
}

class UpdateMemorizationStatusUseCase @Inject constructor(private val repository: MemorizationRepository) {
    suspend operator fun invoke(ayahId: Int, status: MemorizationStatus) = repository.setStatus(ayahId, status)
}

class RateReviewUseCase @Inject constructor(private val repository: MemorizationRepository) {
    suspend operator fun invoke(ayahId: Int, rating: DifficultyRating, today: LocalDate, sessionId: String? = null) =
        repository.recordReview(ayahId, rating, today, sessionId)
}

class CreateMemorizationSessionUseCase @Inject constructor(private val repository: MemorizationRepository) {
    suspend operator fun invoke(
        surahId: Int,
        startAyah: Int,
        endAyah: Int,
        reciterId: String,
        repeatCount: RepeatCount,
        mode: MemorizationMode,
    ): MemorizationSession = repository.createSession(surahId, startAyah, endAyah, reciterId, repeatCount, mode)
}

class ObserveMemorizationSessionsUseCase @Inject constructor(private val repository: MemorizationRepository) {
    operator fun invoke(): Flow<List<MemorizationSession>> = repository.observeSessions()
}

class GetMemorizationSessionUseCase @Inject constructor(private val repository: MemorizationRepository) {
    suspend operator fun invoke(sessionId: String): MemorizationSession? = repository.getSession(sessionId)
}

class ObserveAyahMemorizationProgressUseCase @Inject constructor(private val repository: MemorizationRepository) {
    operator fun invoke(ayahId: Int): Flow<MemorizationProgress?> = repository.observeProgress(ayahId)
}

class GenerateDueReviewsUseCase @Inject constructor(
    private val repository: MemorizationRepository,
    private val engine: com.quoren.app.domain.memorization.SpacedRepetitionEngine,
) {
    operator fun invoke(today: LocalDate): Flow<List<MemorizationProgress>> = repository.observeProgress().map { progress ->
        val dueIds = engine.dueOnOrBefore(progress.map { it.toReviewState() }, today).mapTo(HashSet()) { it.ayahId }
        progress.filter { it.ayahId in dueIds }
    }

    private fun MemorizationProgress.toReviewState() = com.quoren.app.domain.memorization.AyahReviewState(
        ayahId = ayahId,
        status = status,
        scheduleStage = scheduleStage,
        dueDate = dueDate,
        lastReviewedDate = lastReviewedDate,
        reviewCount = reviewCount,
        consecutiveHardRatings = consecutiveHardRatings,
        isWeak = isWeak,
    )
}

class UpdateMemorizationSessionPositionUseCase @Inject constructor(private val repository: MemorizationRepository) {
    suspend operator fun invoke(sessionId: String, ayahNumber: Int) = repository.updateSessionPosition(sessionId, ayahNumber)
}

class CompleteMemorizationSessionUseCase @Inject constructor(private val repository: MemorizationRepository) {
    suspend operator fun invoke(sessionId: String) = repository.completeSession(sessionId)
}

class ObserveMemorizationCountsUseCase @Inject constructor(private val repository: MemorizationRepository) {
    operator fun invoke(today: LocalDate): Flow<MemorizationCounts> = repository.observeCounts(today)
}

class ObserveDailyGoalUseCase @Inject constructor(private val repository: MemorizationRepository) {
    operator fun invoke(): Flow<DailyGoal> = repository.observeDailyGoal()
}

class UpdateDailyGoalUseCase @Inject constructor(private val repository: MemorizationRepository) {
    suspend operator fun invoke(target: Int) = repository.setDailyGoal(target)
}

class CompleteDailyGoalAyahUseCase @Inject constructor(private val repository: MemorizationRepository) {
    suspend operator fun invoke(ayahId: Int, today: LocalDate) = repository.completeAyahForToday(ayahId, today)
}

class ObserveReviewHistoryUseCase @Inject constructor(private val repository: MemorizationRepository) {
    operator fun invoke(limit: Int = 100): Flow<List<ReviewRecord>> = repository.observeReviewHistory(limit)
}
