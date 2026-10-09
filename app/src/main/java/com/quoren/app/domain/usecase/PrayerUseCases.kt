package com.quoren.app.domain.usecase

import com.quoren.app.domain.model.PrayerLocation
import com.quoren.app.domain.model.PrayerSettings
import com.quoren.app.domain.model.PrayerCitySuggestion
import com.quoren.app.domain.model.PrayerLandmark
import com.quoren.app.domain.repository.PrayerConfiguration
import com.quoren.app.domain.repository.PrayerCitySearchRepository
import com.quoren.app.domain.repository.PrayerRepository
import com.quoren.app.domain.repository.PrayerNotificationScheduler
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObservePrayerConfigurationUseCase @Inject constructor(private val prayers: PrayerRepository) {
    operator fun invoke(): Flow<PrayerConfiguration> = prayers.observeConfiguration()
}

class SavePrayerLocationUseCase @Inject constructor(private val prayers: PrayerRepository) {
    suspend operator fun invoke(location: PrayerLocation?) = prayers.saveLocation(location)
}

class UpdatePrayerSettingsUseCase @Inject constructor(private val prayers: PrayerRepository) {
    suspend operator fun invoke(settings: PrayerSettings) = prayers.updateSettings(settings)
}

class GetCurrentDeviceLocationUseCase @Inject constructor(private val prayers: PrayerRepository) {
    suspend operator fun invoke(): PrayerLocation? = prayers.getCurrentDeviceLocation()
}

class SchedulePrayerNotificationsUseCase @Inject constructor(private val scheduler: PrayerNotificationScheduler) {
    fun ensureDailyRefresh() = scheduler.ensureDailyRefresh()
    suspend operator fun invoke(configuration: PrayerConfiguration) = scheduler.schedule(configuration)
    suspend fun previewAdhan() = scheduler.previewAdhan()
}

class SearchPrayerCitiesUseCase @Inject constructor(private val cities: PrayerCitySearchRepository) {
    suspend operator fun invoke(query: String): List<PrayerCitySuggestion> = cities.searchCities(query)
    suspend fun findMainMosque(location: PrayerLocation): PrayerLandmark? = cities.findMainMosque(location)
}