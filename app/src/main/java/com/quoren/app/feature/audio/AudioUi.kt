package com.quoren.app.feature.audio

import androidx.annotation.StringRes
import com.quoren.app.R
import com.quoren.app.domain.model.AudioError
import com.quoren.app.domain.model.RepeatCount

@StringRes
fun AudioError.messageRes(): Int = when (this) {
    AudioError.NO_INTERNET -> R.string.err_no_internet
    AudioError.SERVER_UNAVAILABLE -> R.string.err_server
    AudioError.DOWNLOAD_FAILED -> R.string.err_download
    AudioError.STORAGE_FULL -> R.string.err_storage
    AudioError.CORRUPTED_FILE -> R.string.err_corrupted
    AudioError.TIMEOUT -> R.string.err_timeout
    AudioError.UNKNOWN -> R.string.err_unknown
}

fun formatDuration(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    return "%d:%02d".format(totalSeconds / 60, totalSeconds % 60)
}

@StringRes
fun RepeatCount.labelRes(): Int = if (times == null) R.string.repeat_unlimited else R.string.repeat_times
