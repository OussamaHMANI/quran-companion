package com.quoren.app.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Criteria
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.quoren.app.domain.model.AdhanSound
import com.quoren.app.domain.model.AsrMadhab
import com.quoren.app.domain.model.PrayerCalculationMethod
import com.quoren.app.domain.model.PrayerLocation
import com.quoren.app.domain.model.PrayerOffsets
import com.quoren.app.domain.model.PrayerSettings
import com.quoren.app.domain.model.SilentModeBehavior
import com.quoren.app.domain.repository.PrayerConfiguration
import com.quoren.app.domain.repository.PrayerRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.prayerDataStore: DataStore<Preferences> by preferencesDataStore("prayer_settings")

@Singleton
class PrayerRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : PrayerRepository {

    private val methodKey = stringPreferencesKey("calculation_method")
    private val madhabKey = stringPreferencesKey("asr_madhab")
    private val adhanKey = stringPreferencesKey("adhan_sound")
    private val silentModeKey = stringPreferencesKey("silent_mode")
    private val reminderKey = intPreferencesKey("reminder_minutes")
    private val notificationsKey = booleanPreferencesKey("notifications_enabled")
    private val adhanEnabledKey = booleanPreferencesKey("adhan_enabled")
    private val latitudeKey = doublePreferencesKey("location_latitude")
    private val longitudeKey = doublePreferencesKey("location_longitude")
    private val cityKey = stringPreferencesKey("location_city")
    private val regionKey = stringPreferencesKey("location_region")
    private val countryKey = stringPreferencesKey("location_country")
    private val timezoneKey = stringPreferencesKey("location_timezone")
    private val mosqueKey = stringPreferencesKey("location_mosque")
    private val offsetKeys = mapOf(
        "fajr" to intPreferencesKey("offset_fajr"),
        "sunrise" to intPreferencesKey("offset_sunrise"),
        "dhuhr" to intPreferencesKey("offset_dhuhr"),
        "asr" to intPreferencesKey("offset_asr"),
        "maghrib" to intPreferencesKey("offset_maghrib"),
        "isha" to intPreferencesKey("offset_isha"),
    )

    override fun observeConfiguration(): Flow<PrayerConfiguration> =
        context.prayerDataStore.data.map { preferences -> preferences.toConfiguration() }

    override suspend fun saveLocation(location: PrayerLocation?) {
        context.prayerDataStore.edit { preferences ->
            if (location == null) {
                preferences.remove(latitudeKey)
                preferences.remove(longitudeKey)
                preferences.remove(cityKey)
                preferences.remove(regionKey)
                preferences.remove(countryKey)
                preferences.remove(timezoneKey)
                preferences.remove(mosqueKey)
            } else {
                preferences[latitudeKey] = location.latitude
                preferences[longitudeKey] = location.longitude
                preferences[cityKey] = location.city
                preferences[regionKey] = location.region
                preferences[countryKey] = location.country
                preferences[timezoneKey] = location.timeZoneId
                location.mosqueName?.let { preferences[mosqueKey] = it } ?: preferences.remove(mosqueKey)
            }
        }
    }

    override suspend fun updateSettings(settings: PrayerSettings) {
        context.prayerDataStore.edit { preferences ->
            preferences[methodKey] = settings.calculationMethod.name
            preferences[madhabKey] = settings.asrMadhab.name
            preferences[adhanKey] = settings.adhanSound.name
            preferences[silentModeKey] = settings.silentModeBehavior.name
            preferences[reminderKey] = settings.reminderMinutesBefore.coerceIn(0, 60)
            preferences[notificationsKey] = settings.notificationsEnabled
            preferences[adhanEnabledKey] = settings.adhanEnabled
            preferences[offsetKeys.getValue("fajr")] = settings.offsets.fajrMinutes
            preferences[offsetKeys.getValue("sunrise")] = settings.offsets.sunriseMinutes
            preferences[offsetKeys.getValue("dhuhr")] = settings.offsets.dhuhrMinutes
            preferences[offsetKeys.getValue("asr")] = settings.offsets.asrMinutes
            preferences[offsetKeys.getValue("maghrib")] = settings.offsets.maghribMinutes
            preferences[offsetKeys.getValue("isha")] = settings.offsets.ishaMinutes
        }
    }

    override suspend fun getCurrentDeviceLocation(): PrayerLocation? = withContext(Dispatchers.IO) {
        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) return@withContext null

        val manager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return@withContext null
        val cached = runCatching {
            manager.getProviders(true).mapNotNull { provider -> manager.getLastKnownLocation(provider) }
                .maxByOrNull(Location::getTime)
        }.getOrNull()
        val location = cached ?: requestSingleLocation(manager) ?: return@withContext null
        val address = runCatching {
            @Suppress("DEPRECATION")
            Geocoder(context, Locale.getDefault()).getFromLocation(location.latitude, location.longitude, 1)?.firstOrNull()
        }.getOrNull()
        location.toPrayerLocation(address)
    }

    private suspend fun requestSingleLocation(manager: LocationManager): Location? =
        withTimeoutOrNull(20_000L) {
            suspendCancellableCoroutine { continuation ->
                if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ) {
                    continuation.resume(null)
                    return@suspendCancellableCoroutine
                }
                val provider = runCatching { manager.getBestProvider(Criteria(), true) }.getOrNull()
                if (provider == null) {
                    continuation.resume(null)
                    return@suspendCancellableCoroutine
                }
                val listener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        runCatching { manager.removeUpdates(this) }
                        if (continuation.isActive) continuation.resume(location)
                    }
                    @Deprecated("Deprecated by Android")
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit
                    override fun onProviderEnabled(provider: String) = Unit
                    override fun onProviderDisabled(provider: String) = Unit
                }
                continuation.invokeOnCancellation { runCatching { manager.removeUpdates(listener) } }
                runCatching {
                    manager.requestLocationUpdates(provider, 0L, 0f, listener, Looper.getMainLooper())
                }.onFailure {
                    if (continuation.isActive) continuation.resume(null)
                }
            }
        }

    private fun Location.toPrayerLocation(address: Address?): PrayerLocation = PrayerLocation(
        latitude = latitude,
        longitude = longitude,
        city = address?.locality ?: address?.subAdminArea ?: "${"%.4f".format(latitude)}, ${"%.4f".format(longitude)}",
        region = address?.adminArea.orEmpty(),
        country = address?.countryName.orEmpty(),
        timeZoneId = TimeZone.getDefault().id,
        mosqueName = null,
    )

    private fun Preferences.toConfiguration(): PrayerConfiguration {
        val location = runCatching {
            val latitude = this[latitudeKey] ?: return@runCatching null
            val longitude = this[longitudeKey] ?: return@runCatching null
            PrayerLocation(
                latitude,
                longitude,
                this[cityKey].orEmpty(),
                this[regionKey].orEmpty(),
                this[countryKey].orEmpty(),
                this[timezoneKey] ?: TimeZone.getDefault().id,
                this[mosqueKey],
            )
        }.getOrNull()
        val offsets = PrayerOffsets(
            fajrMinutes = (this[offsetKeys.getValue("fajr")] ?: 0).coerceIn(-60, 60),
            sunriseMinutes = (this[offsetKeys.getValue("sunrise")] ?: 0).coerceIn(-60, 60),
            dhuhrMinutes = (this[offsetKeys.getValue("dhuhr")] ?: 0).coerceIn(-60, 60),
            asrMinutes = (this[offsetKeys.getValue("asr")] ?: 0).coerceIn(-60, 60),
            maghribMinutes = (this[offsetKeys.getValue("maghrib")] ?: 0).coerceIn(-60, 60),
            ishaMinutes = (this[offsetKeys.getValue("isha")] ?: 0).coerceIn(-60, 60),
        )
        return PrayerConfiguration(
            location = location,
            settings = PrayerSettings(
                calculationMethod = enumOrDefault(this[methodKey], PrayerCalculationMethod.MUSLIM_WORLD_LEAGUE),
                asrMadhab = enumOrDefault(this[madhabKey], com.quoren.app.domain.model.AsrMadhab.SHAFI),
                offsets = offsets,
                adhanSound = enumOrDefault(this[adhanKey], AdhanSound.NOTIFICATION_ONLY),
                silentModeBehavior = enumOrDefault(this[silentModeKey], SilentModeBehavior.RESPECT_SILENT),
                reminderMinutesBefore = (this[reminderKey] ?: 0).coerceIn(0, 60),
                notificationsEnabled = this[notificationsKey] ?: false,
                adhanEnabled = this[adhanEnabledKey] ?: false,
            ),
        )
    }

    private inline fun <reified E : Enum<E>> enumOrDefault(value: String?, default: E): E =
        value?.let { runCatching { enumValueOf<E>(it) }.getOrNull() } ?: default
}