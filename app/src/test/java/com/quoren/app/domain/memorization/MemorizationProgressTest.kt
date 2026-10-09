package com.quoren.app.domain.memorization

import org.junit.Assert.assertEquals
import org.junit.Test

class MemorizationProgressTest {

    @Test
    fun dailyGoalCompletionDoesNotPenalizeMissedDays() {
        val goal = com.quoren.app.domain.model.DailyGoal(3, 1, java.time.LocalDate.of(2026, 10, 8))
        assertEquals(false, goal.isComplete)
    }

    @Test
    fun fiveReviewDifficultyValuesAreNotNeededTheRatingsRemainThreeLevel() {
        assertEquals(3, ReviewDifficulty.entries.size)
    }
}
