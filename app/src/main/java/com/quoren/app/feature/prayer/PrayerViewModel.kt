package com.quoren.app.feature.prayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quoren.app.domain.model.NextPrayer
import com.quoren.app.domain.model.PrayerCalculationMethod
import com.quoren.app.domain.model.PrayerCitySuggestion
import com.quoren.app.domain.model.PrayerLandmark
import com.quoren.app.domain.model.PrayerLocation
import com.quoren.app.domain.model.PrayerName
import com.quoren.app.domain.model.PrayerSchedule
import com.quoren.app.domain.model.PrayerSettings
import com.quoren.app.domain.prayer.PrayerTimeEngine
import com.quoren.app.domain.repository.PrayerConfiguration
import com.quoren.app.domain.usecase.GetCurrentDeviceLocationUseCase
import com.quoren.app.domain.usecase.ObservePrayerConfigurationUseCase
import com.quoren.app.domain.usecase.SavePrayerLocationUseCase
import com.quoren.app.domain.usecase.SchedulePrayerNotificationsUseCase
import com.quoren.app.domain.usecase.SearchPrayerCitiesUseCase
import com.quoren.app.domain.usecase.UpdatePrayerSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

data class PrayerScreenState(
    val configuration: PrayerConfiguration = PrayerConfiguration(),
    val schedule: PrayerSchedule? = null,
    val weeklySchedules: List<PrayerSchedule> = emptyList(),
    val isLoadingWeek: Boolean = false,
    val weekLoadError: Boolean = false,
    val monthlySchedules: List<PrayerSchedule> = emptyList(),
    val displayedMonth: YearMonth? = null,
    val isLoadingMonth: Boolean = false,
    val monthLoadError: Boolean = false,
    val nextPrayer: NextPrayer? = null,
    val currentPrayer: PrayerName? = null,
    val now: Instant = Instant.now(),
    val isLocating: Boolean = false,
    val isSearchingCities: Boolean = false,
    val citySuggestions: List<PrayerCitySuggestion> = emptyList(),
    val isSearchingMainMosque: Boolean = false,
    val mainMosqueSuggestion: PrayerLandmark? = null,
    val locationUnavailable: Boolean = false,
    val calculationFailed: Boolean = false,
)

@HiltViewModel
class PrayerViewModel @Inject constructor(
    private val observeConfiguration: ObservePrayerConfigurationUseCase,
    private val saveLocation: SavePrayerLocationUseCase,
    private val updateSettings: UpdatePrayerSettingsUseCase,
    private val getCurrentLocation: GetCurrentDeviceLocationUseCase,
    private val scheduleNotifications: SchedulePrayerNotificationsUseCase,
    private val searchPrayerCities: SearchPrayerCitiesUseCase,
    private val engine: PrayerTimeEngine,
) : ViewModel() {

    private val _state = MutableStateFlow(PrayerScreenState())
    val state: StateFlow<PrayerScreenState> = _state.asStateFlow()
    private var tickerJob: Job? = null
    private var citySearchRequestId = 0
    private var mosqueSearchRequestId = 0
    private var monthRequestId = 0
    private var weekRequestId = 0
    private var prayerScreenActive = false

    init {
        viewModelScope.launch {
            observeConfiguration().collectLatest { configuration ->
                monthRequestId++
                weekRequestId++
                if (prayerScreenActive) launch { runCatching { scheduleNotifications(configuration) } }
                val schedule = calculate(configuration, Instant.now())
                val previous = _state.value
                val selectedMonth = previous.displayedMonth.takeIf {
                    previous.configuration.location == configuration.location
                } ?: schedule?.date?.let(YearMonth::from)
                _state.value = buildState(configuration, schedule, Instant.now(), displayedMonth = selectedMonth).copy(
                    locationUnavailable = false,
                    calculationFailed = configuration.location != null && schedule == null,
                )
            }
        }
    }

    fun startCountdownUpdates(enablePrayerAlerts: Boolean = false) {
        if (enablePrayerAlerts && !prayerScreenActive) {
            prayerScreenActive = true
            viewModelScope.launch {
                scheduleNotifications.ensureDailyRefresh()
                runCatching { scheduleNotifications(_state.value.configuration) }
            }
        }
        if (tickerJob?.isActive == true) return
        tickerJob = viewModelScope.launch {
            while (isActive) {
                delay(1_000)
                val current = _state.value
                val location = current.configuration.location ?: continue
                val now = Instant.now()
                val localDate = now.atZone(zoneFor(location)).toLocalDate()
                val sameLocalDate = current.schedule?.date == localDate
                val schedule = if (sameLocalDate) current.schedule else calculate(current.configuration, now)
                val displayedMonth = current.displayedMonth ?: schedule?.date?.let(YearMonth::from)
                val cachedMonth = current.monthlySchedules.takeIf {
                    it.firstOrNull()?.date?.let(YearMonth::from) == displayedMonth
                }
                _state.value = buildState(
                    current.configuration,
                    schedule,
                    now,
                    current.weeklySchedules.takeIf { sameLocalDate },
                    cachedMonth,
                    displayedMonth,
                ).copy(
                    locationUnavailable = current.locationUnavailable,
                    calculationFailed = schedule == null,
                    isLoadingMonth = current.isLoadingMonth,
                    monthLoadError = current.monthLoadError,
                    isLoadingWeek = current.isLoadingWeek,
                    weekLoadError = current.weekLoadError,
                )
            }
        }
    }

    fun stopCountdownUpdates() {
        tickerJob?.cancel()
        tickerJob = null
        prayerScreenActive = false
    }

    fun loadWeeklySchedules() {
        val current = _state.value
        val location = current.configuration.location ?: return
        val date = current.schedule?.date ?: return
        if (current.weeklySchedules.isNotEmpty() || current.isLoadingWeek) return
        val requestId = ++weekRequestId
        val configuration = current.configuration
        _state.value = current.copy(isLoadingWeek = true, weekLoadError = false)
        viewModelScope.launch {
            val result = runCatching {
                withContext(Dispatchers.Default) { engine.timesForWeek(location, date, configuration.settings) }
            }
            if (requestId == weekRequestId && _state.value.configuration == configuration && _state.value.schedule?.date == date) {
                _state.value = _state.value.copy(
                    weeklySchedules = result.getOrDefault(emptyList()),
                    isLoadingWeek = false,
                    weekLoadError = result.isFailure,
                )
            }
        }
    }

    fun saveManualLocation(location: PrayerLocation, settings: PrayerSettings) {
        viewModelScope.launch {
            runCatching {
                saveLocation(location)
                updateSettings(settings)
            }.onSuccess {
                _state.value = _state.value.copy(locationUnavailable = false, calculationFailed = false)
            }.onFailure {
                _state.value = _state.value.copy(calculationFailed = true)
            }
        }
    }

    fun useCurrentDeviceLocation() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLocating = true, locationUnavailable = false)
            runCatching { getCurrentLocation() }
                .onSuccess { location ->
                    if (location == null) {
                        _state.value = _state.value.copy(isLocating = false, locationUnavailable = true)
                    } else {
                        runCatching { saveLocation(location) }
                            .onSuccess { _state.value = _state.value.copy(isLocating = false) }
                            .onFailure { _state.value = _state.value.copy(isLocating = false, locationUnavailable = true) }
                    }
                }
                .onFailure { _state.value = _state.value.copy(isLocating = false, locationUnavailable = true) }
        }
    }

    fun onLocationPermissionDenied() {
        _state.value = _state.value.copy(isLocating = false, locationUnavailable = true)
    }

    fun changeDisplayedMonth(month: YearMonth) {
        val current = _state.value
        val location = current.configuration.location ?: return
        if (current.displayedMonth == month && current.monthlySchedules.isNotEmpty()) return
        if (current.displayedMonth == month && current.isLoadingMonth) return
        val requestId = ++monthRequestId
        val configuration = current.configuration
        _state.value = current.copy(
            displayedMonth = month,
            monthlySchedules = emptyList(),
            isLoadingMonth = true,
            monthLoadError = false,
        )
        viewModelScope.launch {
            val result = runCatching {
                withContext(Dispatchers.Default) {
                    engine.timesForMonth(location, month.atDay(1), configuration.settings)
                }
            }
            if (requestId == monthRequestId && _state.value.configuration == configuration) {
                _state.value = _state.value.copy(
                    monthlySchedules = result.getOrDefault(emptyList()),
                    isLoadingMonth = false,
                    monthLoadError = result.isFailure,
                )
            }
        }
    }

    fun previewAdhan() {
        viewModelScope.launch { runCatching { scheduleNotifications.previewAdhan() } }
    }

    fun searchCities(query: String) {
        val requestId = ++citySearchRequestId
        mosqueSearchRequestId++
        _state.value = _state.value.copy(
            isSearchingCities = true,
            citySuggestions = emptyList(),
            isSearchingMainMosque = false,
            mainMosqueSuggestion = null,
        )
        viewModelScope.launch {
            val suggestions = runCatching { searchPrayerCities(query) }.getOrDefault(emptyList())
            if (requestId == citySearchRequestId) {
                _state.value = _state.value.copy(isSearchingCities = false, citySuggestions = suggestions)
            }
        }
    }

    fun searchMainMosque(location: PrayerLocation) {
        val requestId = ++mosqueSearchRequestId
        _state.value = _state.value.copy(isSearchingMainMosque = true, mainMosqueSuggestion = null)
        viewModelScope.launch {
            val mosque = runCatching { searchPrayerCities.findMainMosque(location) }.getOrNull()
            if (requestId == mosqueSearchRequestId) {
                _state.value = _state.value.copy(isSearchingMainMosque = false, mainMosqueSuggestion = mosque)
            }
        }
    }

    private suspend fun calculate(configuration: PrayerConfiguration, now: Instant): PrayerSchedule? {
        val location = configuration.location ?: return null
        return withContext(Dispatchers.Default) {
            runCatching { engine.calculate(location, now.atZone(zoneFor(location)).toLocalDate(), configuration.settings) }
                .getOrNull()
        }
    }

    private suspend fun buildState(
        configuration: PrayerConfiguration,
        schedule: PrayerSchedule?,
        now: Instant,
        cachedWeek: List<PrayerSchedule>? = null,
        cachedMonth: List<PrayerSchedule>? = null,
        displayedMonth: YearMonth? = null,
    ) =
        withContext(Dispatchers.Default) {
            val monthToDisplay = displayedMonth ?: schedule?.date?.let(YearMonth::from)
            val week = cachedWeek.orEmpty()
            val month = cachedMonth?.takeIf { it.firstOrNull()?.date?.let(YearMonth::from) == monthToDisplay }.orEmpty()
            val followingFajr = schedule?.let { today ->
                week.firstOrNull { it.date == today.date.plusDays(1) }
                    ?.times?.firstOrNull { it.name == PrayerName.FAJR }
                    ?: configuration.location?.let { location ->
                        engine.calculate(location, today.date.plusDays(1), configuration.settings)
                            .times.firstOrNull { it.name == PrayerName.FAJR }
                    }
            }
            PrayerScreenState(
                configuration = configuration,
                schedule = schedule,
                weeklySchedules = week,
                monthlySchedules = month,
                displayedMonth = monthToDisplay,
                nextPrayer = schedule?.let { engine.nextPrayer(it, now, followingFajr) },
                currentPrayer = schedule?.let { engine.currentPrayer(it, now) },
                now = now,
            )
        }

    private fun zoneFor(location: PrayerLocation): ZoneId =
        runCatching { ZoneId.of(location.timeZoneId) }.getOrDefault(ZoneId.systemDefault())
}