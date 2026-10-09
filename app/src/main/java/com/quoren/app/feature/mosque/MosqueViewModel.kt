package com.quoren.app.feature.mosque

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quoren.app.domain.model.Mosque
import com.quoren.app.domain.model.PrayerCitySuggestion
import com.quoren.app.domain.model.PrayerLocation
import com.quoren.app.domain.repository.PrayerConfiguration
import com.quoren.app.domain.usecase.GetCurrentDeviceLocationUseCase
import com.quoren.app.domain.usecase.ObserveFavoriteMosquesUseCase
import com.quoren.app.domain.usecase.ObserveLastMosqueSearchUseCase
import com.quoren.app.domain.usecase.ObservePrayerConfigurationUseCase
import com.quoren.app.domain.usecase.SearchNearbyMosquesUseCase
import com.quoren.app.domain.usecase.SearchPrayerCitiesUseCase
import com.quoren.app.domain.usecase.ToggleMosqueFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class MosqueScreenState(
    val location: PrayerLocation? = null,
    val mosques: List<Mosque> = emptyList(),
    val favorites: List<Mosque> = emptyList(),
    val citySuggestions: List<PrayerCitySuggestion> = emptyList(),
    val cityQuery: String = "",
    val searchQuery: String = "",
    val radiusMeters: Int = 5_000,
    val favoritesOnly: Boolean = false,
    val loading: Boolean = false,
    val searchingCities: Boolean = false,
    val locationUnavailable: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class MosqueViewModel @Inject constructor(
    observePrayerConfiguration: ObservePrayerConfigurationUseCase,
    private val getCurrentLocation: GetCurrentDeviceLocationUseCase,
    private val searchCities: SearchPrayerCitiesUseCase,
    private val searchNearbyMosques: SearchNearbyMosquesUseCase,
    private val observeFavorites: ObserveFavoriteMosquesUseCase,
    observeLastSearch: ObserveLastMosqueSearchUseCase,
    private val toggleFavorite: ToggleMosqueFavoriteUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(MosqueScreenState())
    val state: StateFlow<MosqueScreenState> = _state.asStateFlow()
    private var citySearchId = 0
    private var citySearchJob: Job? = null
    private var mosqueSearchId = 0
    private var mosqueSearchJob: Job? = null
    private var locationOverride: PrayerLocation? = null

    init {
        viewModelScope.launch {
            observeFavorites().collectLatest { favorites ->
                val favoriteIds = favorites.mapTo(hashSetOf()) { it.id }
                _state.value = _state.value.copy(
                    favorites = favorites,
                    mosques = _state.value.mosques.map { it.copy(isFavorite = it.id in favoriteIds) },
                )
            }
        }
        viewModelScope.launch {
            observeLastSearch().collectLatest { cached ->
                if (locationOverride == null && _state.value.mosques.isEmpty() && cached.isNotEmpty()) {
                    _state.value = _state.value.copy(mosques = cached)
                }
            }
        }
        viewModelScope.launch {
            observePrayerConfiguration().collectLatest { config: PrayerConfiguration ->
                if (locationOverride == null) {
                    if (config.location == null) {
                        _state.value = _state.value.copy(location = null)
                    } else if (_state.value.location == null) {
                        _state.value = _state.value.copy(location = config.location)
                        refresh()
                    }
                }
            }
        }
    }

    fun setSearchQuery(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
    }

    fun setFavoritesOnly(enabled: Boolean) {
        _state.value = _state.value.copy(favoritesOnly = enabled)
    }

    fun setRadius(radiusMeters: Int) {
        if (radiusMeters !in ALLOWED_RADII) return
        _state.value = _state.value.copy(radiusMeters = radiusMeters)
        refresh()
    }

    fun searchCity(query: String) {
        _state.value = _state.value.copy(cityQuery = query)
        citySearchJob?.cancel()
        val requestId = ++citySearchId
        if (query.trim().length < MIN_CITY_QUERY_LENGTH) {
            _state.value = _state.value.copy(searchingCities = false, citySuggestions = emptyList())
            return
        }
        _state.value = _state.value.copy(searchingCities = true, citySuggestions = emptyList())
        citySearchJob = viewModelScope.launch {
            delay(CITY_SEARCH_DEBOUNCE_MS)
            val results = runCatching { searchCities(query) }.getOrDefault(emptyList())
            if (requestId == citySearchId) {
                _state.value = _state.value.copy(citySuggestions = results, searchingCities = false)
            }
        }
    }

    fun selectCity(suggestion: PrayerCitySuggestion) {
        citySearchId++
        mosqueSearchJob?.cancel()
        locationOverride = suggestion.location
        _state.value = _state.value.copy(
            location = suggestion.location,
            mosques = emptyList(),
            cityQuery = "",
            citySuggestions = emptyList(),
            favoritesOnly = false,
            error = null,
        )
        refresh()
    }

    fun useCurrentLocation() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, locationUnavailable = false)
            runCatching { getCurrentLocation() }
                .onSuccess { location ->
                    if (location == null) {
                        _state.value = _state.value.copy(loading = false, locationUnavailable = true)
                    } else {
                        locationOverride = location
                        mosqueSearchJob?.cancel()
                        _state.value = _state.value.copy(
                            location = location,
                            mosques = emptyList(),
                            locationUnavailable = false,
                            error = null,
                        )
                        refresh()
                    }
                }
                .onFailure { _state.value = _state.value.copy(loading = false, locationUnavailable = true) }
        }
    }

    fun onLocationPermissionDenied() {
        _state.value = _state.value.copy(loading = false, locationUnavailable = true)
    }

    fun refresh() {
        val location = _state.value.location ?: return
        val radius = _state.value.radiusMeters
        val requestId = ++mosqueSearchId
        mosqueSearchJob?.cancel()
        mosqueSearchJob = viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                val mosques = withContext(Dispatchers.IO) { searchNearbyMosques(location, radius) }
                if (requestId != mosqueSearchId || _state.value.location != location || _state.value.radiusMeters != radius) return@launch
                val favoriteIds = _state.value.favorites.mapTo(hashSetOf()) { it.id }
                _state.value = _state.value.copy(
                    mosques = mosques.map { it.copy(isFavorite = it.id in favoriteIds) },
                    loading = false,
                    error = null,
                )
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                if (requestId == mosqueSearchId && _state.value.location == location && _state.value.radiusMeters == radius) {
                    _state.value = _state.value.copy(loading = false, error = error.message)
                }
            }
        }
    }

    fun toggleFavorite(mosque: Mosque) {
        viewModelScope.launch { runCatching { toggleFavorite(mosque.copy(isFavorite = !mosque.isFavorite)) } }
    }

    fun visibleMosques(): List<Mosque> {
        val state = _state.value
        val source = if (state.favoritesOnly) state.favorites else state.mosques
        val query = normalize(state.searchQuery)
        return source.filter { mosque ->
            query.isBlank() || normalize(mosque.name).contains(query) || normalize(mosque.address.orEmpty()).contains(query)
        }.sortedBy { it.distanceMeters }
    }

    private fun normalize(value: String): String = value.trim().lowercase()

    companion object {
        private const val MIN_CITY_QUERY_LENGTH = 3
        private const val CITY_SEARCH_DEBOUNCE_MS = 350L
        private val ALLOWED_RADII = setOf(1_000, 5_000, 10_000, 20_000)
    }
}