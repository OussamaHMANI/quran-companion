package com.quoren.app.domain.repository

import com.quoren.app.domain.model.PrayerLocation
import com.quoren.app.domain.model.PrayerCitySuggestion
import com.quoren.app.domain.model.PrayerLandmark
import com.quoren.app.domain.model.PrayerSettings
import kotlinx.coroutines.flow.Flow

data class PrayerConfiguration(
    val location: PrayerLocation? = null,
    val settings: PrayerSettings = PrayerSettings(),
)

interface PrayerRepository {
    fun observeConfiguration(): Flow<PrayerConfiguration>
    suspend fun saveLocation(location: PrayerLocation?)
    suspend fun updateSettings(settings: PrayerSettings)
    suspend fun getCurrentDeviceLocation(): PrayerLocation?
}

interface PrayerNotificationScheduler {
    fun ensureDailyRefresh()
    suspend fun schedule(configuration: PrayerConfiguration)
    suspend fun previewAdhan()
}

interface PrayerCitySearchRepository {
    suspend fun searchCities(query: String): List<PrayerCitySuggestion>
    suspend fun findMainMosque(location: PrayerLocation): PrayerLandmark?
}