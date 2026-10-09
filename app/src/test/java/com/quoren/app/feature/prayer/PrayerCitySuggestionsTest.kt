package com.quoren.app.feature.prayer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PrayerCitySuggestionsTest {

    @Test
    fun suggestsSfaxTunisiaForLatinAndArabicQueries() {
        val latin = PrayerCitySuggestions.search("sfax").single()
        assertEquals("Sfax", latin.location.city)
        assertEquals("Tunisia", latin.location.country)
        assertEquals("Africa/Tunis", latin.location.timeZoneId)
        assertEquals("الجامع الكبير بصفاقس", latin.location.mosqueName)

        val arabic = PrayerCitySuggestions.search("صفاقس").single()
        assertEquals(latin.location, arabic.location)
    }

    @Test
    fun selectingSfaxMosqueUsesItsMappedCoordinates() {
        val suggestion = PrayerCitySuggestions.search("Sfax").single()
        val location = PrayerCitySuggestions.selectMosque(suggestion)
        assertEquals(34.7359089, location.latitude, 0.0000001)
        assertEquals(10.7608362, location.longitude, 0.0000001)
        assertEquals(suggestion.location.mosqueName, location.mosqueName)
    }

    @Test
    fun ignoresShortAndUnmatchedQueries() {
        assertTrue(PrayerCitySuggestions.search("sf").isEmpty())
        assertTrue(PrayerCitySuggestions.search("Marseille").isEmpty())
    }
}