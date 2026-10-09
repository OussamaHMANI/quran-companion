package com.quoren.app.feature.qibla

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quoren.app.domain.model.PrayerLocation
import com.quoren.app.domain.qibla.CompassAccuracy
import com.quoren.app.domain.qibla.QiblaDirection
import com.quoren.app.domain.qibla.QiblaEngine
import com.quoren.app.domain.repository.CompassRepository
import com.quoren.app.domain.usecase.GetCurrentDeviceLocationUseCase
import com.quoren.app.domain.usecase.ObservePrayerConfigurationUseCase
import com.quoren.app.domain.usecase.SavePrayerLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

data class QiblaUiState(
    val location: PrayerLocation? = null,
    val direction: QiblaDirection? = null,
    val headingDegrees: Double? = null,
    val relativeBearingDegrees: Double? = null,
    val accuracy: CompassAccuracy = CompassAccuracy.UNAVAILABLE,
    val isLocating: Boolean = false,
    val locationUnavailable: Boolean = false,
    val compassAvailable: Boolean = false,
)

@HiltViewModel
class QiblaViewModel @Inject constructor(
    observeConfiguration: ObservePrayerConfigurationUseCase,
    private val saveLocation: SavePrayerLocationUseCase,
    private val getCurrentLocation: GetCurrentDeviceLocationUseCase,
    private val compass: CompassRepository,
    private val engine: QiblaEngine,
) : ViewModel() {

    private val _state = MutableStateFlow(QiblaUiState(compassAvailable = compass.isAvailable()))
    val state: StateFlow<QiblaUiState> = _state.asStateFlow()
    private var compassJob: Job? = null
    private var compassRunning = false

    init {
        viewModelScope.launch {
            observeConfiguration().collectLatest { configuration ->
                val location = configuration.location
                if (location == null) {
                    stopCompass()
                    _state.value = _state.value.copy(location = null, direction = null, locationUnavailable = true)
                    return@collectLatest
                }
                setLocation(location)
            }
            }
    }

    fun useCurrentLocation() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLocating = true, locationUnavailable = false)
            runCatching { getCurrentLocation() }
                .onSuccess { location ->
                    if (location == null) {
                        _state.value = _state.value.copy(isLocating = false, locationUnavailable = true)
                    } else {
                        runCatching { saveLocation(location) }
                        setLocation(location)
                    }
                }
                .onFailure { _state.value = _state.value.copy(isLocating = false, locationUnavailable = true) }
        }
    }

    fun onLocationPermissionDenied() {
        _state.value = _state.value.copy(isLocating = false, locationUnavailable = true)
    }

    fun startCompass() {
        val location = _state.value.location ?: return
        if (!_state.value.compassAvailable || compassRunning) return
        compassRunning = true
        compassJob?.cancel()
        compassJob = viewModelScope.launch {
            compass.observeHeading(location.latitude, location.longitude).collectLatest { reading ->
                val bearing = _state.value.direction?.bearingDegrees
                _state.value = _state.value.copy(
                    headingDegrees = reading.headingDegrees,
                    relativeBearingDegrees = reading.headingDegrees?.let { heading -> bearing?.let { engine.relativeBearing(it, heading) } },
                    accuracy = reading.accuracy,
                )
            }
        }
        _state.value = _state.value.copy(compassAvailable = true)
    }

    fun stopCompass() {
        compassRunning = false
        compassJob?.cancel()
        compassJob = null
        _state.value = _state.value.copy(headingDegrees = null, relativeBearingDegrees = null)
    }

    private suspend fun setLocation(location: PrayerLocation) {
        val direction = withContext(Dispatchers.Default) {
            runCatching { engine.calculate(location.latitude, location.longitude) }.getOrNull()
        }
        val heading = _state.value.headingDegrees
        _state.value = _state.value.copy(
            location = location,
            direction = direction,
            relativeBearingDegrees = heading?.let { direction?.let { qibla -> engine.relativeBearing(qibla.bearingDegrees, it) } },
            isLocating = false,
            locationUnavailable = false,
        )
    }
}