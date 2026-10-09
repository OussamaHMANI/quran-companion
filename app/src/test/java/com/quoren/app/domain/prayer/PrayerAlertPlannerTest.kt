package com.quoren.app.domain.prayer

import com.quoren.app.domain.model.AdhanSound
import com.quoren.app.domain.model.PrayerLocation
import com.quoren.app.domain.model.PrayerSettings
import com.quoren.app.domain.repository.PrayerConfiguration
import java.time.Instant
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PrayerAlertPlannerTest {

    private val location = PrayerLocation(35.8256, 10.6369, "Sousse", "Sousse", "Tunisia", "Africa/Tunis")
    private val engine = PrayerTimeEngine()
    private val planner = PrayerAlertPlanner(engine)

    @Test
    fun earlyReminderAndAdhanAreSeparateEventsAtTheirOwnTimes() {
        val settings = PrayerSettings(
            reminderMinutesBefore = 10,
            notificationsEnabled = true,
            adhanEnabled = true,
            adhanSound = AdhanSound.OTHER,
        )
        val day = LocalDate.of(2026, 10, 10)
        val schedule = engine.calculate(location, day, settings)
        val dhuhr = schedule.times.first { it.prayerName() == com.quoren.app.domain.model.PrayerName.DHUHR }
        val now = dhuhr.instant.minusSeconds(11 * 60L)

        val events = planner.plan(PrayerConfiguration(location, settings), now, days = 1)
            .filter { it.prayer == com.quoren.app.domain.model.PrayerName.DHUHR }

        assertEquals(2, events.size)
        assertEquals(dhuhr.instant.minusSeconds(600).epochSecond, events.single { !it.playAdhan }.triggerAt.epochSecond)
        assertEquals(dhuhr.instant.epochSecond, events.single { it.playAdhan }.triggerAt.epochSecond)
    }

    @Test
    fun notificationOnlyHasAnAtTimeEventWithoutAdhanPlayback() {
        val settings = PrayerSettings(notificationsEnabled = true, reminderMinutesBefore = 0)
        val now = Instant.parse("2026-10-10T00:00:00Z")
        val events = planner.plan(PrayerConfiguration(location, settings), now, days = 1)

        assertFalse(events.isEmpty())
        assertTrue(events.none { it.playAdhan })
        assertTrue(events.all { it.reminderMinutes == 0 })
    }

    @Test
    fun enabledAdhanSchedulesEvenWhenOptionalNotificationsAreDisabled() {
        val settings = PrayerSettings(notificationsEnabled = false, adhanEnabled = true, adhanSound = AdhanSound.OTHER)
        val alerts = planner.plan(PrayerConfiguration(location, settings), Instant.parse("2026-10-10T00:00:00Z"), days = 1)
        assertTrue(alerts.isNotEmpty())
        assertTrue(alerts.all { it.playAdhan })
    }

    @Test
    fun disabledNotificationsAndAdhanProduceNoAlerts() {
        val settings = PrayerSettings(notificationsEnabled = false, adhanEnabled = false)
        assertTrue(planner.plan(PrayerConfiguration(location, settings), Instant.EPOCH).isEmpty())
    }

    private fun com.quoren.app.domain.model.PrayerTime.prayerName() = name
}