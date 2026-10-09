package com.quoren.app.domain.model

import java.time.LocalDate

enum class MemorizationMode { READ_LISTEN, LISTEN_RECALL, VOICE_RECALL, PROGRESSIVE_HIDING, GROUP }

enum class DifficultyRating { HARD, MEDIUM, EASY }

data class MemorizationSession(
    val sessionId: String,
    val surahId: Int,
    val startAyah: Int,
    val endAyah: Int,
    val reciterId: String,
    val repeatCount: RepeatCount,
    val mode: MemorizationMode,
    val currentAyah: Int,
    val createdAt: Long,
    val lastOpenedAt: Long,
    val completedAt: Long? = null,
)

data class MemorizationProgress(
    val ayahId: Int,
    val status: com.quoren.app.domain.memorization.MemorizationStatus,
    val scheduleStage: Int,
    val dueDate: LocalDate,
    val lastReviewedDate: LocalDate?,
    val reviewCount: Int,
    val consecutiveHardRatings: Int,
    val isWeak: Boolean,
)

data class ReviewRecord(
    val id: Long,
    val ayahId: Int,
    val sessionId: String?,
    val difficulty: DifficultyRating,
    val reviewedAt: Long,
    val dueDate: LocalDate,
)

data class MemorizationCounts(
    val newCount: Int = 0,
    val learningCount: Int = 0,
    val reviewCount: Int = 0,
    val memorizedCount: Int = 0,
    val dueCount: Int = 0,
    val weakCount: Int = 0,
    val totalReviews: Int = 0,
    val completedSessions: Int = 0,
    val studiedMinutes: Int = 0,
)

data class DailyGoal(val ayahTarget: Int, val ayahsCompleted: Int, val updatedDate: LocalDate) {
    val isComplete: Boolean get() = ayahsCompleted >= ayahTarget
}
