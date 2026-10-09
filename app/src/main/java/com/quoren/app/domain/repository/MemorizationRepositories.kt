package com.quoren.app.domain.repository

import com.quoren.app.domain.memorization.MemorizationStatus
import com.quoren.app.domain.model.DailyGoal
import com.quoren.app.domain.model.DifficultyRating
import com.quoren.app.domain.model.MemorizationCounts
import com.quoren.app.domain.model.MemorizationMode
import com.quoren.app.domain.model.MemorizationProgress
import com.quoren.app.domain.model.MemorizationSession
import com.quoren.app.domain.model.RepeatCount
import com.quoren.app.domain.model.ReviewRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface MemorizationRepository {
    fun observeProgress(): Flow<List<MemorizationProgress>>
    fun observeProgress(ayahId: Int): Flow<MemorizationProgress?>
    suspend fun setStatus(ayahId: Int, status: MemorizationStatus)
    suspend fun recordReview(ayahId: Int, rating: DifficultyRating, reviewedAt: LocalDate, sessionId: String?): MemorizationProgress

    fun observeSessions(): Flow<List<MemorizationSession>>
    suspend fun createSession(
        surahId: Int,
        startAyah: Int,
        endAyah: Int,
        reciterId: String,
        repeatCount: RepeatCount,
        mode: MemorizationMode,
    ): MemorizationSession
    suspend fun updateSessionPosition(sessionId: String, ayahNumber: Int)
    suspend fun completeSession(sessionId: String)
    suspend fun getSession(sessionId: String): MemorizationSession?
    suspend fun deleteSession(sessionId: String)

    fun observeReviewHistory(limit: Int = 100): Flow<List<ReviewRecord>>
    fun observeCounts(today: LocalDate): Flow<MemorizationCounts>
    fun observeDailyGoal(): Flow<DailyGoal>
    suspend fun setDailyGoal(ayahTarget: Int)
    suspend fun completeAyahForToday(ayahId: Int, today: LocalDate)
}
