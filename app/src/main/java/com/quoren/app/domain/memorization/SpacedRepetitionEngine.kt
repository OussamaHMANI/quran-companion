package com.quoren.app.domain.memorization

import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/** Difficulty reported by the learner after recalling an ayah. */
enum class ReviewDifficulty { HARD, MEDIUM, EASY }

enum class MemorizationStatus { NEW, LEARNING, REVIEW, MEMORIZED }

data class AyahReviewState(
    val ayahId: Int,
    val status: MemorizationStatus = MemorizationStatus.NEW,
    val scheduleStage: Int = 0,
    val dueDate: LocalDate,
    val lastReviewedDate: LocalDate? = null,
    val reviewCount: Int = 0,
    val consecutiveHardRatings: Int = 0,
    val isWeak: Boolean = false,
)

data class ReviewOutcome(val state: AyahReviewState, val intervalDays: Int)

/** Pure spaced-repetition planner; LocalDate prevents daylight-saving changes from shifting a review day. */
@Singleton
class SpacedRepetitionEngine @Inject constructor() {

    fun newAyah(ayahId: Int, today: LocalDate): AyahReviewState =
        AyahReviewState(ayahId = ayahId, dueDate = today)

    fun recordReview(
        current: AyahReviewState,
        difficulty: ReviewDifficulty,
        reviewedOn: LocalDate,
    ): ReviewOutcome {
        val nextStage = when (difficulty) {
            ReviewDifficulty.HARD -> (current.scheduleStage - 1).coerceAtLeast(0)
            ReviewDifficulty.MEDIUM, ReviewDifficulty.EASY ->
                (current.scheduleStage + 1).coerceAtMost(INTERVAL_DAYS.lastIndex)
        }
        val interval = if (difficulty == ReviewDifficulty.HARD) 1 else INTERVAL_DAYS[nextStage]
        val hardCount = if (difficulty == ReviewDifficulty.HARD) current.consecutiveHardRatings + 1 else 0
        val next = current.copy(
            status = when {
                current.status == MemorizationStatus.MEMORIZED && difficulty == ReviewDifficulty.EASY -> MemorizationStatus.MEMORIZED
                nextStage >= INTERVAL_DAYS.lastIndex && difficulty != ReviewDifficulty.HARD -> MemorizationStatus.MEMORIZED
                current.status == MemorizationStatus.NEW -> MemorizationStatus.LEARNING
                else -> MemorizationStatus.REVIEW
            },
            scheduleStage = nextStage,
            dueDate = reviewedOn.plusDays(interval.toLong()),
            lastReviewedDate = reviewedOn,
            reviewCount = current.reviewCount + 1,
            consecutiveHardRatings = hardCount,
            isWeak = if (difficulty == ReviewDifficulty.HARD) hardCount >= WEAK_HARD_RATING_THRESHOLD else false,
        )
        return ReviewOutcome(next, interval)
    }

    fun dueOnOrBefore(states: Iterable<AyahReviewState>, date: LocalDate): List<AyahReviewState> =
        states.asSequence()
            .filter { it.status != MemorizationStatus.NEW && it.dueDate <= date }
            .sortedWith(compareBy<AyahReviewState> { it.dueDate }.thenBy { it.ayahId })
            .toList()

    companion object {
        val INTERVAL_DAYS = listOf(0, 1, 3, 7, 14, 30, 60, 90)
        const val WEAK_HARD_RATING_THRESHOLD = 3
    }
}
