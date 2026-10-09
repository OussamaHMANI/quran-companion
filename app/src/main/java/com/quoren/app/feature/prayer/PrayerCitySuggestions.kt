package com.quoren.app.feature.prayer

import com.quoren.app.domain.model.PrayerLocation
import java.text.Normalizer
import java.time.ZoneId
import java.util.Locale

data class PrayerCitySuggestion(
    val location: PrayerLocation,
    val mosqueLatitude: Double,
    val mosqueLongitude: Double,
)

object PrayerCitySuggestions {
    private val suggestions = listOf(
        PrayerCitySuggestion(
            location = PrayerLocation(
                latitude = 34.7394361,
                longitude = 10.7604024,
                city = "Sfax",
                region = "Sfax",
                country = "Tunisia",
                timeZoneId = "Africa/Tunis",
                mosqueName = "الجامع الكبير بصفاقس",
            ),
            mosqueLatitude = 34.7359089,
            mosqueLongitude = 10.7608362,
        ),
    )

    fun search(query: String): List<PrayerCitySuggestion> {
        val normalized = normalize(query)
        if (normalized.length < 3) return emptyList()
        return suggestions.filter { suggestion ->
            normalize(suggestion.location.city).contains(normalized) ||
                normalize(suggestion.location.country).contains(normalized) ||
                normalize("صفاقس").contains(normalized)
        }
    }

    fun selectMosque(suggestion: PrayerCitySuggestion): PrayerLocation = suggestion.location.copy(
        latitude = suggestion.mosqueLatitude,
        longitude = suggestion.mosqueLongitude,
    )

    private fun normalize(value: String): String = Normalizer.normalize(value.lowercase(Locale.ROOT), Normalizer.Form.NFD)
        .filterNot { Character.getType(it) == Character.NON_SPACING_MARK.toInt() }
        .replace('أ', 'ا').replace('إ', 'ا').replace('آ', 'ا')
        .replace('ى', 'ي').replace(Regex("\\s+"), " ").trim()
}