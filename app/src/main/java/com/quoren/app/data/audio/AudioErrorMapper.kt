package com.quoren.app.data.audio

import androidx.media3.common.PlaybackException
import com.quoren.app.domain.model.AudioError

object AudioErrorMapper {
    fun fromPlayerErrorCode(code: Int): AudioError = when (code) {
        PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED -> AudioError.NO_INTERNET
        PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT,
        PlaybackException.ERROR_CODE_TIMEOUT,
        -> AudioError.TIMEOUT
        PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS,
        PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND,
        PlaybackException.ERROR_CODE_IO_INVALID_HTTP_CONTENT_TYPE,
        -> AudioError.SERVER_UNAVAILABLE
        PlaybackException.ERROR_CODE_PARSING_CONTAINER_MALFORMED,
        PlaybackException.ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED,
        PlaybackException.ERROR_CODE_DECODING_FAILED,
        PlaybackException.ERROR_CODE_DECODER_INIT_FAILED,
        -> AudioError.CORRUPTED_FILE
        else -> AudioError.UNKNOWN
    }
}
