package com.quoren.app.domain.audio

import com.quoren.app.domain.model.CurrentAyah
import com.quoren.app.domain.model.PlaybackState
import com.quoren.app.domain.model.PlaybackStatus
import com.quoren.app.domain.repository.AudioPlayerManager
import com.quoren.app.domain.repository.QuranRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/** Maps playback state to the ayah being recited; emits only when the ayah changes. */
@Singleton
class AyahSynchronizationEngine @Inject constructor(
    player: AudioPlayerManager,
    private val quran: QuranRepository,
) {
    private var startIds: Map<Int, Int>? = null

    val currentAyah: Flow<CurrentAyah?> = player.state
        .map { it.activeAyahKey() }
        .distinctUntilChanged()
        .map { key -> key?.let { locate(it.first, it.second) } }

    private fun PlaybackState.activeAyahKey(): Pair<Int, Int>? {
        val surah = surahId ?: return null
        val ayah = ayahNumber ?: return null
        return if (status == PlaybackStatus.IDLE || status == PlaybackStatus.ENDED) null else surah to ayah
    }

    private suspend fun locate(surahId: Int, ayahNumber: Int): CurrentAyah? {
        val starts = startIds ?: run {
            var next = 1
            quran.getSurahs().associate { s -> s.id to next.also { next += s.numberOfAyahs } }.also { startIds = it }
        }
        val id = (starts[surahId] ?: return null) + ayahNumber - 1
        val ayah = quran.getAyah(id) ?: return null
        return CurrentAyah(id, surahId, ayahNumber, ayah.page)
    }
}
