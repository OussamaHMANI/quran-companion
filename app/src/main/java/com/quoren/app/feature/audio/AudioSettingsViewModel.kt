package com.quoren.app.feature.audio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quoren.app.domain.model.AudioSettings
import com.quoren.app.domain.model.PlaybackRequest
import com.quoren.app.domain.model.PlaybackStatus
import com.quoren.app.domain.model.Reciter
import com.quoren.app.domain.usecase.AudioCacheUseCase
import com.quoren.app.domain.usecase.GetRecitersUseCase
import com.quoren.app.domain.usecase.ObserveAudioSettingsUseCase
import com.quoren.app.domain.usecase.ObservePlaybackStateUseCase
import com.quoren.app.domain.usecase.PlayAudioUseCase
import com.quoren.app.domain.usecase.PlaybackControlsUseCase
import com.quoren.app.domain.usecase.SetDefaultReciterUseCase
import com.quoren.app.domain.usecase.UpdateAudioSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AudioSettingsUiState(
    val settings: AudioSettings = AudioSettings(),
    val reciters: List<Reciter> = emptyList(),
    val defaultReciterId: String = "",
    val cacheSizeBytes: Long = 0,
)

@HiltViewModel
class AudioSettingsViewModel @Inject constructor(
    observeSettings: ObserveAudioSettingsUseCase,
    getReciters: GetRecitersUseCase,
    private val setDefaultReciter: SetDefaultReciterUseCase,
    private val updateSettings: UpdateAudioSettingsUseCase,
    private val cache: AudioCacheUseCase,
    private val observePlayback: ObservePlaybackStateUseCase,
    private val playAudio: PlayAudioUseCase,
    private val playbackControls: PlaybackControlsUseCase,
) : ViewModel() {

    private val reciters = MutableStateFlow<List<Reciter>>(emptyList())
    private val cacheSize = MutableStateFlow(0L)

    val state: StateFlow<AudioSettingsUiState> =
        combine(observeSettings(), reciters, cacheSize) { settings, list, size ->
            AudioSettingsUiState(
                settings = settings,
                reciters = list,
                defaultReciterId = settings.defaultReciterId ?: list.firstOrNull()?.id.orEmpty(),
                cacheSizeBytes = size,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AudioSettingsUiState())

    init {
        viewModelScope.launch { reciters.value = getReciters() }
        refreshCacheSize()
    }

    fun onReciterSelected(id: String) {
        viewModelScope.launch {
            setDefaultReciter(id)
            val current = observePlayback().first()
            val surahId = current.surahId ?: return@launch
            val ayahNumber = current.ayahNumber ?: current.rangeStart.takeIf { it > 0 } ?: return@launch
            if (!current.isActive) return@launch
            runCatching {
                playAudio(
                    PlaybackRequest(
                        surahId = surahId,
                        startAyah = ayahNumber,
                        endAyah = current.rangeEnd.takeIf { it >= ayahNumber },
                        reciterId = id,
                        ayahRepeatOverride = current.ayahRepeat,
                    ),
                )
                if (current.status == PlaybackStatus.PAUSED) playbackControls.pause()
            }
        }
    }

    fun update(transform: (AudioSettings) -> AudioSettings) {
        viewModelScope.launch { updateSettings(transform) }
    }

    fun onClearCache() {
        viewModelScope.launch {
            cache.clear()
            refreshCacheSize()
        }
    }

    private fun refreshCacheSize() {
        viewModelScope.launch { cacheSize.update { cache.sizeBytes() } }
    }
}
