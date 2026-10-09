package com.quoren.app.domain.prayer

import com.quoren.app.domain.model.PrayerName
import com.quoren.app.domain.model.AdhanSound
import com.quoren.app.domain.repository.PrayerConfiguration
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

data class PrayerAlert(
    val prayer: PrayerName,
    val triggerAt: Instant,
    val reminderMinutes: Int,
    val playAdhan: Boolean,
)

@Singleton
class PrayerAlertPlanner @Inject constructor(
    private val engine: PrayerTimeEngine,
) {

    fun plan(configuration: PrayerConfiguration, now: Instant, days: Int = HORIZON_DAYS): List<PrayerAlert> {
        require(days > 0)
        val location = configuration.location ?: return emptyList()
        val settings = configuration.settings
        if (!settings.notificationsEnabled && !settings.adhanEnabled) return emptyList()

        val zone = runCatching { ZoneId.of(location.timeZoneId) }.getOrDefault(ZoneId.systemDefault())
        val firstDate = now.atZone(zone).toLocalDate()
        return (0 until days).flatMap { dayOffset ->
            engine.calculate(location, firstDate.plusDays(dayOffset.toLong()), settings).times
                .asSequence()
                .filter { it.name != PrayerName.SUNRISE }
                .flatMap { prayerTime ->
                    buildList {
                        val reminderMinutes = settings.reminderMinutesBefore
                        if (settings.notificationsEnabled && reminderMinutes > 0) {
                            val reminderAt = prayerTime.instant.minusSeconds(reminderMinutes * 60L)
                            if (reminderAt.isAfter(now)) add(PrayerAlert(prayerTime.name, reminderAt, reminderMinutes, false))
                        } else if (settings.notificationsEnabled && !settings.adhanEnabled && prayerTime.instant.isAfter(now)) {
                            add(PrayerAlert(prayerTime.name, prayerTime.instant, 0, false))
                        }
                        if (settings.adhanEnabled && settings.adhanSound == AdhanSound.OTHER && prayerTime.instant.isAfter(now)) {
                            add(PrayerAlert(prayerTime.name, prayerTime.instant, 0, true))
                        }
                    }.asSequence()
                }
                .toList()
        }
    }

    companion object {
        private const val HORIZON_DAYS = 14
    }
}