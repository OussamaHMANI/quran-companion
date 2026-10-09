package com.quoren.app.domain.memorization

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SpacedRepetitionEngineTest {

    private val engine = SpacedRepetitionEngine()
    private val today = LocalDate.of(2026, 10, 8)

    @Test
    fun newAyahIsDueOnDayZero() {
        val state = engine.newAyah(255, today)
        assertEquals(MemorizationStatus.NEW, state.status)
        assertEquals(today, state.dueDate)
        assertEquals(0, state.reviewCount)
    }

    @Test
    fun successfulReviewsFollowTheSpacedSchedule() {
        var state = engine.newAyah(1, today)
        val expectedIntervals = listOf(1, 3, 7, 14, 30, 60, 90)
        for (interval in expectedIntervals) {
            val outcome = engine.recordReview(state, ReviewDifficulty.MEDIUM, state.dueDate)
            assertEquals(interval, outcome.intervalDays)
            state = outcome.state
        }
        assertEquals(MemorizationStatus.MEMORIZED, state.status)
        assertEquals(7, state.reviewCount)
    }

    @Test
    fun hardRatingMovesBackAndSchedulesTomorrow() {
        var state = engine.newAyah(1, today)
        state = engine.recordReview(state, ReviewDifficulty.EASY, today).state
        state = engine.recordReview(state, ReviewDifficulty.EASY, state.dueDate).state
        val outcome = engine.recordReview(state, ReviewDifficulty.HARD, state.dueDate)

        assertEquals(1, outcome.intervalDays)
        assertEquals(1, outcome.state.scheduleStage)
        assertEquals(MemorizationStatus.REVIEW, outcome.state.status)
        assertFalse(outcome.state.isWeak)
    }

    @Test
    fun threeConsecutiveHardRatingsMarkAyahWeakUntilSuccessfulReview() {
        var state = engine.newAyah(7, today)
        repeat(3) { state = engine.recordReview(state, ReviewDifficulty.HARD, state.dueDate).state }
        assertTrue(state.isWeak)

        state = engine.recordReview(state, ReviewDifficulty.MEDIUM, state.dueDate).state
        assertFalse(state.isWeak)
        assertEquals(0, state.consecutiveHardRatings)
    }

    @Test
    fun dueQueueExcludesNewAyahsAndSortsByDateThenAyahId() {
        val late = engine.newAyah(2, today).copy(status = MemorizationStatus.REVIEW, dueDate = today.plusDays(2))
        val dueB = engine.newAyah(3, today).copy(status = MemorizationStatus.REVIEW)
        val dueA = engine.newAyah(1, today).copy(status = MemorizationStatus.LEARNING)
        val fresh = engine.newAyah(4, today)

        assertEquals(listOf(1, 3), engine.dueOnOrBefore(listOf(late, dueB, fresh, dueA), today).map { it.ayahId })
        assertEquals(listOf(1, 3, 2), engine.dueOnOrBefore(listOf(late, dueB, fresh, dueA), today.plusDays(2)).map { it.ayahId })
    }

    @Test
    fun allowsAnEarlyUserRequestedReviewAndReschedulesIt() {
        val state = engine.newAyah(1, today).copy(status = MemorizationStatus.REVIEW, dueDate = today.plusDays(2))
        val outcome = engine.recordReview(state, ReviewDifficulty.EASY, today)
        assertEquals(today.plusDays(1), outcome.state.dueDate)
        assertEquals(today, outcome.state.lastReviewedDate)
    }
}
