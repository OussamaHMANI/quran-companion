package com.quoren.app.domain.audio

import com.quoren.app.domain.model.Ayah
import com.quoren.app.domain.model.JuzStart
import com.quoren.app.domain.model.PlaybackPosition
import com.quoren.app.domain.model.PlaybackRequest
import com.quoren.app.domain.model.PlaybackState
import com.quoren.app.domain.model.PlaybackStatus
import com.quoren.app.domain.model.RevelationType
import com.quoren.app.domain.model.Surah
import com.quoren.app.domain.repository.AudioPlayerManager
import com.quoren.app.domain.repository.QuranRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AyahSynchronizationEngineTest {

    private class FakePlayer : AudioPlayerManager {
        override val state = MutableStateFlow(PlaybackState())
        override fun positionFlow(): Flow<PlaybackPosition> = emptyFlow()
        override suspend fun play(request: PlaybackRequest) = Unit
        override fun pause() = Unit
        override fun resume() = Unit
        override fun stop() = Unit
        override fun seekToProgress(progress: Float) = Unit
        override fun next() = Unit
        override fun previous() = Unit
    }

    private class FakeQuran : QuranRepository {
        private val surahs = listOf(
            Surah(1, "a", "a", "a", 7, RevelationType.MECCAN, 1),
            Surah(2, "b", "b", "b", 286, RevelationType.MEDINAN, 2),
        )

        override suspend fun getSurahs() = surahs
        override suspend fun getJuzStarts(): List<JuzStart> = emptyList()
        override suspend fun getPage(page: Int): List<Ayah> = emptyList()
        override suspend fun getAyah(ayahId: Int) = Ayah(ayahId, 0, 0, "x", page = ayahId / 10 + 1, juz = 1, hizb = 1)
        override suspend fun getBasmala() = ""
        override suspend fun search(query: String, limit: Int): List<Ayah> = emptyList()
    }

    private fun playing(surah: Int, ayah: Int, status: PlaybackStatus = PlaybackStatus.PLAYING) =
        PlaybackState(status = status, surahId = surah, ayahNumber = ayah)

    @Test
    fun mapsSurahAndAyahNumberToGlobalAyahId() = runTest {
        val player = FakePlayer()
        val engine = AyahSynchronizationEngine(player, FakeQuran())
        val collected = mutableListOf<Int?>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { engine.currentAyah.collect { collected += it?.ayahId } }

        player.state.value = playing(1, 3)
        player.state.value = playing(2, 1)
        player.state.value = playing(2, 5)
        job.cancel()

        assertEquals(listOf(null, 3, 8, 12), collected)
    }

    @Test
    fun emitsOnlyWhenAyahChanges() = runTest {
        val player = FakePlayer()
        val engine = AyahSynchronizationEngine(player, FakeQuran())
        val collected = mutableListOf<Int?>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { engine.currentAyah.collect { collected += it?.ayahId } }

        player.state.value = playing(1, 2)
        player.state.value = playing(1, 2, PlaybackStatus.PAUSED)
        player.state.value = playing(1, 2, PlaybackStatus.BUFFERING)
        job.cancel()

        assertEquals(listOf(null, 2), collected)
    }

    @Test
    fun clearsHighlightWhenIdleOrEnded() = runTest {
        val player = FakePlayer()
        val engine = AyahSynchronizationEngine(player, FakeQuran())
        val collected = mutableListOf<Int?>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { engine.currentAyah.collect { collected += it?.ayahId } }

        player.state.value = playing(1, 2)
        player.state.value = playing(1, 2, PlaybackStatus.ENDED)
        job.cancel()

        assertEquals(listOf(null, 2, null), collected)
    }
}
