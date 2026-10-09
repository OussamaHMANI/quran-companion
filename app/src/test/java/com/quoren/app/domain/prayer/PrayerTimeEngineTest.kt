package com.quoren.app.domain.prayer

import com.quoren.app.domain.model.PrayerLocation
import com.quoren.app.domain.model.PrayerOffsets
import com.quoren.app.domain.model.PrayerSettings
import com.quoren.app.domain.model.PrayerName
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class PrayerTimeEngineTest {

    private val engine = PrayerTimeEngine()
    private val location = PrayerLocation(
        latitude = 51.5074,
        longitude = -0.1278,
        city = "London",
        region = "England",
        country = "United Kingdom",
        timeZoneId = "Europe/London",
    )
    private val date = LocalDate.of(2025, 6, 1)

    @Test
    fun appliesEachConfiguredOffsetExactlyOnce() {
        val baseline = engine.calculate(location, date, PrayerSettings())
        val offsets = PrayerOffsets(
            fajrMinutes = 11,
            sunriseMinutes = -7,
            dhuhrMinutes = 5,
            asrMinutes = 13,
            maghribMinutes = -3,
            ishaMinutes = 9,
        )
        val adjusted = engine.calculate(location, date, PrayerSettings(offsets = offsets))
        val expectedOffsets = listOf(11, -7, 5, 13, -3, 9)

        PrayerName.entries.forEachIndexed { index, prayer ->
            val before = baseline.times[index]
            val after = adjusted.times[index]
            assertEquals(prayer, after.name)
            assertEquals(expectedOffsets[index] * 60L, after.instant.epochSecond - before.instant.epochSecond)
        }
    }

    @Test
    fun nextPrayerSkipsSunriseAndUsesTomorrowFajrAcrossDaylightSavingBoundary() {
        val date = LocalDate.of(2025, 3, 30)
        val settings = PrayerSettings()
        val today = engine.calculate(location, date, settings)
        val afterIsha = today.times.last { it.name == PrayerName.ISHA }.instant.plusSeconds(60)
        val expectedFajr = engine.calculate(location, date.plusDays(1), settings)
            .times.first { it.name == PrayerName.FAJR }

        val next = engine.nextPrayer(today, afterIsha)
        assertEquals(PrayerName.FAJR, next.prayer.name)
        assertEquals(expectedFajr.instant.epochSecond, next.startsAt.epochSecond)
        assertEquals(next.startsAt, next.prayer.instant)

        val cachedNext = engine.nextPrayer(today, afterIsha, expectedFajr)
        assertEquals(expectedFajr, cachedNext.prayer)

        val beforeSunrise = today.times.first { it.name == PrayerName.SUNRISE }.instant.minusSeconds(60)
        assertEquals(PrayerName.DHUHR, engine.nextPrayer(today, beforeSunrise).prayer.name)
        val afterSunrise = today.times.first { it.name == PrayerName.SUNRISE }.instant.plusSeconds(60)
        assertEquals(null, engine.currentPrayer(today, afterSunrise))
    }

    @Test
    fun monthlyScheduleUsesTheSelectedLocalMonthLengthAndDates() {
        val february = engine.timesForMonth(location, LocalDate.of(2026, 2, 12), PrayerSettings())
        assertEquals(28, february.size)
        assertEquals(LocalDate.of(2026, 2, 1), february.first().date)
        assertEquals(LocalDate.of(2026, 2, 28), february.last().date)
        assertEquals(PrayerName.entries, february.first().times.map { it.name })
    }
}