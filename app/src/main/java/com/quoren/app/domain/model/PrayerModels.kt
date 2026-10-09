package com.quoren.app.domain.model

import java.time.Instant
import java.time.LocalDate

data class PrayerLocation(
    val latitude: Double,
    val longitude: Double,
    val city: String,
    val region: String,
    val country: String,
    val timeZoneId: String,
    val mosqueName: String? = null,
) {
    init {
        require(latitude in -90.0..90.0)
        require(longitude in -180.0..180.0)
        require(timeZoneId.isNotBlank())
    }
}

enum class PrayerCalculationMethod { MUSLIM_WORLD_LEAGUE, EGYPTIAN, UMM_AL_QURA, ISNA, GULF_REGION, FRANCE_MOSQUE }
enum class AsrMadhab { SHAFI, HANAFI }
enum class PrayerName { FAJR, SUNRISE, DHUHR, ASR, MAGHRIB, ISHA }
enum class PrayerPeriodStatus { PAST, CURRENT, UPCOMING }
enum class AdhanSound { MECCA, MADINAH, SHORT, OTHER, NOTIFICATION_ONLY }
enum class SilentModeBehavior { PLAY_ANYWAY, RESPECT_SILENT, NOTIFICATION_ONLY }

data class PrayerOffsets(
    val fajrMinutes: Int = 0,
    val sunriseMinutes: Int = 0,
    val dhuhrMinutes: Int = 0,
    val asrMinutes: Int = 0,
    val maghribMinutes: Int = 0,
    val ishaMinutes: Int = 0,
) {
    init {
        listOf(fajrMinutes, sunriseMinutes, dhuhrMinutes, asrMinutes, maghribMinutes, ishaMinutes)
            .forEach { require(it in -60..60) }
    }
}

data class PrayerSettings(
    val calculationMethod: PrayerCalculationMethod = PrayerCalculationMethod.MUSLIM_WORLD_LEAGUE,
    val asrMadhab: AsrMadhab = AsrMadhab.SHAFI,
    val offsets: PrayerOffsets = PrayerOffsets(),
    val adhanSound: AdhanSound = AdhanSound.NOTIFICATION_ONLY,
    val silentModeBehavior: SilentModeBehavior = SilentModeBehavior.RESPECT_SILENT,
    val reminderMinutesBefore: Int = 0,
    val notificationsEnabled: Boolean = false,
    val adhanEnabled: Boolean = false,
)

data class PrayerTime(val name: PrayerName, val instant: Instant)

data class PrayerSchedule(
    val date: LocalDate,
    val location: PrayerLocation,
    val settings: PrayerSettings,
    val times: List<PrayerTime>,
) {
    init {
        require(times.map { it.name } == PrayerName.entries.toList())
    }
}

data class NextPrayer(
    val prayer: PrayerTime,
    val startsAt: Instant,
    val remainingSeconds: Long,
)
