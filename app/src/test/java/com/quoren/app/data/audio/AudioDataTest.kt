package com.quoren.app.data.audio

import androidx.media3.common.PlaybackException
import com.quoren.app.data.download.DownloadFailure
import com.quoren.app.data.download.SurahDownloadWorker
import com.quoren.app.domain.model.AudioError
import com.quoren.app.domain.model.PlaybackPosition
import com.quoren.app.domain.model.PlaybackState
import com.quoren.app.domain.model.PlaybackStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class AudioDataTest {

    @Test
    fun catalogHasRequestedRecitersWithUniqueIds() {
        val ids = ReciterCatalog.all.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
        assertEquals(8, ids.size)
        assertTrue(ReciterCatalog.all.any { it.id == ReciterCatalog.DEFAULT_ID })
        assertTrue(ReciterCatalog.all.all { !it.audioBaseUrl.endsWith("/") && it.audioBaseUrl.startsWith("https://") })
    }

    @Test
    fun ayahUrlsUseZeroPaddedSurahAndAyah() {
        assertEquals("001001.mp3", ReciterCatalog.ayahFileName(1, 1))
        assertEquals("114006.mp3", ReciterCatalog.ayahFileName(114, 6))
        assertEquals("https://x/002255.mp3", ReciterCatalog.ayahUrl("https://x", 2, 255))
    }

    @Test
    fun basmalaIntroIsOnlyUsedAtTheStartOfSurahsThatHaveOne() {
        assertTrue(ReciterCatalog.needsBasmalaIntro(4, 1))
        assertFalse(ReciterCatalog.needsBasmalaIntro(1, 1))
        assertFalse(ReciterCatalog.needsBasmalaIntro(9, 1))
        assertFalse(ReciterCatalog.needsBasmalaIntro(4, 2))
    }

    @Test
    fun playerErrorsMapToArabicFacingCategories() {
        assertEquals(AudioError.NO_INTERNET, AudioErrorMapper.fromPlayerErrorCode(PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED))
        assertEquals(AudioError.TIMEOUT, AudioErrorMapper.fromPlayerErrorCode(PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT))
        assertEquals(AudioError.SERVER_UNAVAILABLE, AudioErrorMapper.fromPlayerErrorCode(PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS))
        assertEquals(AudioError.CORRUPTED_FILE, AudioErrorMapper.fromPlayerErrorCode(PlaybackException.ERROR_CODE_PARSING_CONTAINER_MALFORMED))
        assertEquals(AudioError.UNKNOWN, AudioErrorMapper.fromPlayerErrorCode(PlaybackException.ERROR_CODE_UNSPECIFIED))
    }

    @Test
    fun downloadFailuresAreClassified() {
        assertEquals(AudioError.NO_INTERNET, SurahDownloadWorker.classify(UnknownHostException()))
        assertEquals(AudioError.TIMEOUT, SurahDownloadWorker.classify(SocketTimeoutException()))
        assertEquals(AudioError.STORAGE_FULL, SurahDownloadWorker.classify(IOException("write failed: ENOSPC (No space left on device)")))
        assertEquals(AudioError.CORRUPTED_FILE, SurahDownloadWorker.classify(DownloadFailure(AudioError.CORRUPTED_FILE)))
        assertEquals(AudioError.DOWNLOAD_FAILED, SurahDownloadWorker.classify(IOException("boom")))
    }

    @Test
    fun playbackStateDerivedValues() {
        val state = PlaybackState(status = PlaybackStatus.PLAYING, ayahNumber = 5, rangeStart = 3, rangeEnd = 9)
        assertEquals(7, state.ayahCount)
        assertEquals(2, state.ayahIndex)
        assertTrue(state.isPlaying)
        assertFalse(PlaybackState().isActive)
        assertFalse(PlaybackState(status = PlaybackStatus.PAUSED).isPlaying)
    }

    @Test
    fun playbackPositionProgressCoversTheWholeAyahQueue() {
        assertEquals(0f, PlaybackPosition(mediaItemIndex = 0, mediaItemCount = 4).progress, 0.001f)
        assertEquals(0.375f, PlaybackPosition(500, 1_000, 1, 4).progress, 0.001f)
        assertEquals(0.75f, PlaybackPosition(mediaItemIndex = 3, mediaItemCount = 4).progress, 0.001f)
    }
}
