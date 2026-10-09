package com.quoren.app.data.mosque

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.quoren.app.domain.model.Mosque
import com.quoren.app.domain.model.PrayerLocation
import com.quoren.app.domain.qibla.GeoDistance
import com.quoren.app.domain.repository.MosqueRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.roundToInt
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject

private val Context.mosqueDataStore: DataStore<Preferences> by preferencesDataStore("nearby_mosques")

@Singleton
class MosqueRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val http: OkHttpClient,
) : MosqueRepository {

    private val favoritesKey = stringPreferencesKey("favorites_json")
    private val cacheKey = stringPreferencesKey("last_search_json")
    private val nearbyCache = ConcurrentHashMap<NearbySearchKey, NearbyCacheEntry>()

    override suspend fun searchNearby(location: PrayerLocation, radiusMeters: Int): List<Mosque> = withContext(Dispatchers.IO) {
        require(radiusMeters in 500..50_000)
        val key = NearbySearchKey(
            latitudeE4 = (location.latitude * COORDINATE_CACHE_SCALE).roundToInt(),
            longitudeE4 = (location.longitude * COORDINATE_CACHE_SCALE).roundToInt(),
            radiusMeters = radiusMeters,
        )
        val now = System.currentTimeMillis()
        nearbyCache[key]?.takeIf { now - it.createdAt < MEMORY_CACHE_TTL_MS }?.let { cached ->
            val favoriteIds = favoritesSnapshot().mapTo(mutableSetOf()) { it.id }
            return@withContext cached.mosques.map { it.copy(isFavorite = it.id in favoriteIds) }
        }
        val query = overpassQuery(location.latitude, location.longitude, radiusMeters)
        val online = runCatching { queryOverpass(query, location) }.getOrNull()
        if (online != null) {
            val favoriteIds = favoritesSnapshot().mapTo(mutableSetOf()) { it.id }
            val results = online.map { it.copy(isFavorite = it.id in favoriteIds) }.sortedBy { it.distanceMeters }
            nearbyCache[key] = NearbyCacheEntry(now, results)
            context.mosqueDataStore.edit { it[cacheKey] = encodeMosques(results) }
            return@withContext results
        }

        val cached = parseMosques(context.mosqueDataStore.data.map { it[cacheKey] }.first())
        val favoriteIds = favoritesSnapshot().mapTo(mutableSetOf()) { it.id }
        cached.map { mosque ->
            mosque.copy(
                distanceMeters = GeoDistance.meters(location.latitude, location.longitude, mosque.latitude, mosque.longitude),
                isFavorite = mosque.id in favoriteIds,
            )
        }.filter { it.distanceMeters <= radiusMeters }.sortedBy { it.distanceMeters }
            .also { nearbyCache[key] = NearbyCacheEntry(now, it) }
    }

    override fun observeFavorites(): Flow<List<Mosque>> = context.mosqueDataStore.data.map { preferences ->
        parseMosques(preferences[favoritesKey]).map { it.copy(isFavorite = true) }.sortedBy { it.name }
    }

    override fun observeLastSearch(): Flow<List<Mosque>> = context.mosqueDataStore.data.map { preferences ->
        parseMosques(preferences[cacheKey])
    }

    override suspend fun toggleFavorite(mosque: Mosque) {
        context.mosqueDataStore.edit { preferences ->
            val favorites = parseMosques(preferences[favoritesKey]).associateBy { it.id }.toMutableMap()
            if (mosque.id in favorites) favorites.remove(mosque.id) else favorites[mosque.id] = mosque.copy(isFavorite = true)
            preferences[favoritesKey] = encodeMosques(favorites.values.toList())
        }
    }

    override suspend fun replaceFavorites(mosques: List<Mosque>) {
        require(mosques.size <= MAX_FAVORITES)
        context.mosqueDataStore.edit { preferences ->
            preferences[favoritesKey] = encodeMosques(mosques.distinctBy { it.id }.map { it.copy(isFavorite = true) })
        }
    }

    private fun queryOverpass(query: String, origin: PrayerLocation): List<Mosque> {
        val request = Request.Builder()
            .url(OVERPASS_URL)
            .header("User-Agent", USER_AGENT)
            .post(FormBody.Builder().add("data", query).build())
            .build()
        http.newCall(request).execute().use { response ->
            if (!response.isSuccessful) error("Mosque search unavailable: HTTP ${response.code}")
            val root = JSONObject(response.body?.string().orEmpty())
            val elements = root.optJSONArray("elements") ?: return emptyList()
            val results = ArrayList<Mosque>()
            for (index in 0 until elements.length()) {
                val element = elements.optJSONObject(index) ?: continue
                val tags = element.optJSONObject("tags") ?: continue
                val latitude = element.optDouble("lat", Double.NaN).takeIf(Double::isFinite)
                    ?: element.optJSONObject("center")?.optDouble("lat", Double.NaN)?.takeIf(Double::isFinite)
                    ?: continue
                val longitude = element.optDouble("lon", Double.NaN).takeIf(Double::isFinite)
                    ?: element.optJSONObject("center")?.optDouble("lon", Double.NaN)?.takeIf(Double::isFinite)
                    ?: continue
                if (latitude !in -90.0..90.0 || longitude !in -180.0..180.0) continue
                val type = element.optString("type").takeIf(String::isNotBlank) ?: continue
                val id = element.optLong("id", -1L).takeIf { it > 0 } ?: continue
                val name = tags.optString("name:ar").takeIf(String::isNotBlank)
                    ?: tags.optString("name").takeIf(String::isNotBlank)
                    ?: tags.optString("name:fr").takeIf(String::isNotBlank)
                    ?: tags.optString("operator").takeIf(String::isNotBlank)
                    ?: "مسجد"
                val address = tags.optString("addr:full").takeIf(String::isNotBlank)
                    ?: listOf(tags.optString("addr:street"), tags.optString("addr:housenumber"), tags.optString("addr:city"))
                        .filter(String::isNotBlank).joinToString(" ").takeIf(String::isNotBlank)
                val phone = tags.optString("contact:phone").takeIf(String::isNotBlank)
                    ?: tags.optString("phone").takeIf(String::isNotBlank)
                val website = tags.optString("contact:website").takeIf(String::isNotBlank)
                    ?: tags.optString("website").takeIf(String::isNotBlank)
                results += Mosque(
                    id = "$type/$id",
                    name = name,
                    latitude = latitude,
                    longitude = longitude,
                    address = address,
                    phone = phone,
                    website = website,
                    distanceMeters = GeoDistance.meters(origin.latitude, origin.longitude, latitude, longitude),
                )
            }
            return results.distinctBy { it.id }.sortedBy { it.distanceMeters }.take(MAX_RESULTS)
        }
    }

    private suspend fun favoritesSnapshot(): List<Mosque> =
        parseMosques(context.mosqueDataStore.data.map { it[favoritesKey] }.first())

    private fun parseMosques(json: String?): List<Mosque> = runCatching {
        val array = JSONArray(json ?: "[]")
        (0 until array.length()).mapNotNull { index ->
            val item = array.optJSONObject(index) ?: return@mapNotNull null
            Mosque(
                id = item.optString("id"),
                name = item.optString("name"),
                latitude = item.optDouble("lat", Double.NaN),
                longitude = item.optDouble("lon", Double.NaN),
                address = item.optString("address").takeIf(String::isNotBlank),
                phone = item.optString("phone").takeIf(String::isNotBlank),
                website = item.optString("website").takeIf(String::isNotBlank),
                distanceMeters = item.optDouble("distance", 0.0).coerceAtLeast(0.0),
                isFavorite = item.optBoolean("favorite", false),
            )
        }
    }.getOrDefault(emptyList())

    private fun encodeMosques(mosques: List<Mosque>): String = JSONArray().apply {
        mosques.forEach { mosque ->
            put(
                JSONObject()
                    .put("id", mosque.id)
                    .put("name", mosque.name)
                    .put("lat", mosque.latitude)
                    .put("lon", mosque.longitude)
                    .put("address", mosque.address ?: "")
                    .put("phone", mosque.phone ?: "")
                    .put("website", mosque.website ?: "")
                    .put("distance", mosque.distanceMeters)
                    .put("favorite", mosque.isFavorite),
            )
        }
    }.toString()

    private fun overpassQuery(latitude: Double, longitude: Double, radiusMeters: Int): String = """
                [out:json][timeout:12];
        (
                    nwr(around:$radiusMeters,$latitude,$longitude)[amenity=place_of_worship][religion~"muslim|islam"];
                    nwr(around:$radiusMeters,$latitude,$longitude)[amenity=mosque];
                    nwr(around:$radiusMeters,$latitude,$longitude)[building=mosque];
        );
                out center tags $MAX_RESULTS;
    """.trimIndent()

        private data class NearbySearchKey(val latitudeE4: Int, val longitudeE4: Int, val radiusMeters: Int)
        private data class NearbyCacheEntry(val createdAt: Long, val mosques: List<Mosque>)

    companion object {
        private const val OVERPASS_URL = "https://overpass-api.de/api/interpreter"
        private const val USER_AGENT = "QuorenAndroid/0.1.0 (nearby mosque search)"
        private const val MAX_RESULTS = 100
        private const val MAX_FAVORITES = 2_000
        private const val COORDINATE_CACHE_SCALE = 10_000.0
        private const val MEMORY_CACHE_TTL_MS = 5 * 60 * 1_000L
    }
}