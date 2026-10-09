package com.quoren.app.data.prayer

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.quoren.app.domain.model.PrayerCitySuggestion
import com.quoren.app.domain.model.PrayerLandmark
import com.quoren.app.domain.model.PrayerLocation
import com.quoren.app.domain.repository.PrayerCitySearchRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.ZoneId
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

@Singleton
class AndroidPrayerCitySearchRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val http: OkHttpClient,
) : PrayerCitySearchRepository {

    private val timezoneCache = ConcurrentHashMap<String, String>()

    override suspend fun searchCities(query: String): List<PrayerCitySuggestion> = withContext(Dispatchers.IO) {
        if (query.trim().length < MIN_QUERY_LENGTH || !Geocoder.isPresent()) return@withContext emptyList()
        val suggestions = runCatching { geocoder().getFromLocationName(query.trim(), MAX_CITY_RESULTS).orEmpty() }
            .getOrDefault(emptyList())
            .mapNotNull(::toCitySuggestion)
            .distinctBy { "${it.location.city.lowercase()}|${it.location.region.lowercase()}|${it.location.country.lowercase()}" }
        coroutineScope {
            suggestions.map { suggestion ->
                async(Dispatchers.IO) {
                    val zone = lookupTimeZone(suggestion.location.latitude, suggestion.location.longitude)
                    if (zone == null) suggestion else suggestion.copy(location = suggestion.location.copy(timeZoneId = zone))
                }
            }.awaitAll()
        }
    }

    override suspend fun findMainMosque(location: PrayerLocation): PrayerLandmark? = withContext(Dispatchers.IO) {
        if (!Geocoder.isPresent()) return@withContext null
        val query = "main mosque in ${location.city}, ${location.country}"
        runCatching { geocoder().getFromLocationName(query, 1).orEmpty().firstOrNull() }
            .getOrNull()
            ?.toLandmark()
    }

    @Suppress("DEPRECATION")
    private fun geocoder() = Geocoder(context, Locale.getDefault())

    private fun toCitySuggestion(address: Address): PrayerCitySuggestion? {
        val city = address.locality ?: address.subAdminArea ?: address.featureName ?: return null
        val country = address.countryName.orEmpty()
        val location = PrayerLocation(
            latitude = address.latitude,
            longitude = address.longitude,
            city = city,
            region = address.adminArea.orEmpty(),
            country = country,
            timeZoneId = ZoneId.systemDefault().id,
        )
        return PrayerCitySuggestion(location)
    }

    private fun Address.toLandmark(): PrayerLandmark? {
        val name = featureName?.takeIf { it.isNotBlank() } ?: thoroughfare?.takeIf { it.isNotBlank() } ?: return null
        return PrayerLandmark(name, latitude, longitude)
    }

    private fun lookupTimeZone(latitude: Double, longitude: Double): String? {
        val cacheKey = "${latitude.toInt()}:${longitude.toInt()}"
        timezoneCache[cacheKey]?.let { return it }
        val timezone = runCatching {
            val url = "https://timeapi.io/api/TimeZone/coordinate?latitude=$latitude&longitude=$longitude"
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "QuorenAndroid/0.1.0")
                .build()
            http.newBuilder().callTimeout(4, TimeUnit.SECONDS).build().newCall(request).execute().use { response ->
                if (!response.isSuccessful) return null
                val candidate = JSONObject(response.body?.string().orEmpty()).optString("timeZone")
                ZoneId.of(candidate).id
            }
        }.getOrNull()
        if (timezone != null) timezoneCache[cacheKey] = timezone
        return timezone
    }

    companion object {
        private const val MIN_QUERY_LENGTH = 3
        private const val MAX_CITY_RESULTS = 5
    }
}