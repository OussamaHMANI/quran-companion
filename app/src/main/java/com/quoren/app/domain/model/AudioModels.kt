package com.quoren.app.domain.model

data class Reciter(
    val id: String,
    val arabicName: String,
    val englishName: String,
    val riwaya: String,
    val imageUrl: String?,
    val audioBaseUrl: String,
    /** Lower-bitrate variant used when streaming quality is LOW; null when none exists. */
    val audioBaseUrlLow: String? = null,
)

enum class RepeatCount(val times: Int?) {
    ONE(1), THREE(3), FIVE(5), TEN(10), UNLIMITED(null);

    /** True when [pass] (1-based) is the last pass required. */
    fun isLastPass(pass: Int): Boolean = times != null && pass >= times
}

enum class StreamingQuality { HIGH, LOW }

data class AudioSettings(
    val defaultReciterId: String? = null,
    val ayahRepeat: RepeatCount = RepeatCount.ONE,
    val rangeRepeat: RepeatCount = RepeatCount.ONE,
    val repeatDelaySeconds: Int = 0,
    val streamingQuality: StreamingQuality = StreamingQuality.HIGH,
    val autoDownload: Boolean = false,
    val cacheLimitMb: Int = DEFAULT_CACHE_LIMIT_MB,
) {
    companion object {
        val DELAY_OPTIONS_SECONDS = listOf(0, 1, 2, 3, 5)
        val CACHE_LIMIT_OPTIONS_MB = listOf(128, 256, 512, 1024)
        const val DEFAULT_CACHE_LIMIT_MB = 512
    }
}

enum class AudioError { NO_INTERNET, SERVER_UNAVAILABLE, DOWNLOAD_FAILED, STORAGE_FULL, CORRUPTED_FILE, TIMEOUT, UNKNOWN }

enum class PlaybackStatus { IDLE, BUFFERING, PLAYING, PAUSED, ENDED, ERROR }

/** Plays ayahs [startAyah]..[endAyah] of a surah. Null [endAyah] means "to the end of the surah". */
data class PlaybackRequest(
    val surahId: Int,
    val startAyah: Int = 1,
    val endAyah: Int? = null,
    val reciterId: String? = null,
    val ayahRepeatOverride: RepeatCount? = null,
    val rangeRepeatOverride: RepeatCount? = null,
)

data class PlaybackState(
    val status: PlaybackStatus = PlaybackStatus.IDLE,
    val reciterId: String? = null,
    val reciterName: String? = null,
    val surahId: Int? = null,
    val surahName: String? = null,
    val ayahNumber: Int? = null,
    val rangeStart: Int = 0,
    val rangeEnd: Int = 0,
    val ayahPass: Int = 1,
    val ayahRepeat: RepeatCount = RepeatCount.ONE,
    val rangePass: Int = 1,
    val rangeRepeat: RepeatCount = RepeatCount.ONE,
    val error: AudioError? = null,
) {
    val isActive: Boolean get() = status != PlaybackStatus.IDLE
    val isPlaying: Boolean get() = status == PlaybackStatus.PLAYING || status == PlaybackStatus.BUFFERING
    val ayahCount: Int get() = if (rangeEnd >= rangeStart && rangeStart > 0) rangeEnd - rangeStart + 1 else 0
    val ayahIndex: Int get() = (ayahNumber ?: rangeStart) - rangeStart
}

data class PlaybackPosition(
    val positionMs: Long = 0,
    val durationMs: Long? = null,
    val mediaItemIndex: Int = 0,
    val mediaItemCount: Int = 0,
) {
    val remainingMs: Long? get() = durationMs?.let { (it - positionMs).coerceAtLeast(0) }
    val progress: Float
        get() {
            if (mediaItemCount <= 0) return 0f
            val itemProgress = durationMs?.takeIf { it > 0L }
                ?.let { (positionMs.toFloat() / it).coerceIn(0f, 1f) } ?: 0f
            return ((mediaItemIndex.coerceIn(0, mediaItemCount - 1) + itemProgress) / mediaItemCount)
                .coerceIn(0f, 1f)
        }
}

data class CurrentAyah(val ayahId: Int, val surahId: Int, val ayahNumber: Int, val page: Int)

enum class DownloadStatus { QUEUED, DOWNLOADING, PAUSED, COMPLETED, FAILED }

data class DownloadedAudio(
    val reciterId: String,
    val surahId: Int,
    val localPath: String,
    val fileSize: Long,
    val status: DownloadStatus,
    val downloadedAyahs: Int,
    val totalAyahs: Int,
    val error: AudioError? = null,
) {
    val progress: Float get() = if (totalAyahs == 0) 0f else downloadedAyahs.toFloat() / totalAyahs
}

data class AudioSource(val remoteUrl: String, val localPath: String?)
