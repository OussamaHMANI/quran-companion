package com.quoren.app.feature.audio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quoren.app.domain.model.PlaybackPosition
import com.quoren.app.domain.model.PlaybackState
import com.quoren.app.domain.usecase.ObservePlaybackPositionUseCase
import com.quoren.app.domain.usecase.ObservePlaybackStateUseCase
import com.quoren.app.domain.usecase.PlaybackControlsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    observeState: ObservePlaybackStateUseCase,
    observePosition: ObservePlaybackPositionUseCase,
    private val controls: PlaybackControlsUseCase,
) : ViewModel() {

    val state: StateFlow<PlaybackState> =
        observeState().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PlaybackState())

    /** Polls only while the full player is on screen. */
    val position: StateFlow<PlaybackPosition> =
        observePosition().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PlaybackPosition())

    fun togglePlayPause() {
        if (state.value.isPlaying) controls.pause() else controls.resume()
    }

    fun stop() = controls.stop()
    fun next() = controls.next()
    fun previous() = controls.previous()
    fun seekToProgress(progress: Float) = controls.seekToProgress(progress)
}
