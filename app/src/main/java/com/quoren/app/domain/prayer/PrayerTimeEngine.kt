package com.quoren.app.domain.prayer

import com.batoulapps.adhan.CalculationMethod
import com.batoulapps.adhan.CalculationParameters
import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.HighLatitudeRule
import com.batoulapps.adhan.Madhab
import com.batoulapps.adhan.PrayerAdjustments as AdhanAdjustments
import com.batoulapps.adhan.PrayerTimes as AdhanPrayerTimes
import com.batoulapps.adhan.data.DateComponents
import com.quoren.app.domain.model.AsrMadhab
import com.quoren.app.domain.model.NextPrayer
import com.quoren.app.domain.model.PrayerCalculationMethod
import com.quoren.app.domain.model.PrayerLocation
import com.quoren.app.domain.model.PrayerName
import com.quoren.app.domain.model.PrayerSchedule
import com.quoren.app.domain.model.PrayerSettings
import com.quoren.app.domain.model.PrayerTime
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

data class PrayerAngles(val fajr: Double, val isha: Double)

@Singleton
class PrayerTimeEngine @Inject constructor() {

    fun calculate(location: PrayerLocation, date: LocalDate, settings: PrayerSettings): PrayerSchedule {
        val parameters = calculationParameters(settings)
        val result = AdhanPrayerTimes(
            Coordinates(location.latitude, location.longitude),
            DateComponents(date.year, date.monthValue, date.dayOfMonth),
            parameters,
        )
        val times = listOf(
            PrayerTime(PrayerName.FAJR, result.fajr.toInstant()),
            PrayerTime(PrayerName.SUNRISE, result.sunrise.toInstant()),
            PrayerTime(PrayerName.DHUHR, result.dhuhr.toInstant()),
            PrayerTime(PrayerName.ASR, result.asr.toInstant()),
            PrayerTime(PrayerName.MAGHRIB, result.maghrib.toInstant()),
            PrayerTime(PrayerName.ISHA, result.isha.toInstant()),
        )
        return PrayerSchedule(date, location, settings, times)
    }

    fun nextPrayer(schedule: PrayerSchedule, now: Instant): NextPrayer = nextPrayer(schedule, now, null)

    fun nextPrayer(schedule: PrayerSchedule, now: Instant, followingDayFajr: PrayerTime?): NextPrayer {
        val prayer = schedule.times.firstOrNull {
            it.name != PrayerName.SUNRISE && it.instant.isAfter(now)
        } ?: followingDayFajr
            ?: calculate(schedule.location, schedule.date.plusDays(1), schedule.settings).times.first { it.name == PrayerName.FAJR }
        return NextPrayer(prayer, prayer.instant, (prayer.instant.epochSecond - now.epochSecond).coerceAtLeast(0))
    }

    fun currentPrayer(schedule: PrayerSchedule, now: Instant): PrayerName? =
        schedule.times.lastOrNull { !it.instant.isAfter(now) }
            ?.name?.takeUnless { it == PrayerName.SUNRISE }

    fun timesForWeek(location: PrayerLocation, firstDay: LocalDate, settings: PrayerSettings): List<PrayerSchedule> =
        (0L..6L).map { offset -> calculate(location, firstDay.plusDays(offset), settings) }

    fun timesForMonth(location: PrayerLocation, anyDay: LocalDate, settings: PrayerSettings): List<PrayerSchedule> {
        val firstDay = anyDay.withDayOfMonth(1)
        return (1..firstDay.lengthOfMonth()).map { day -> calculate(location, firstDay.withDayOfMonth(day), settings) }
    }

    private fun calculationParameters(settings: PrayerSettings): CalculationParameters {
        val params = when (settings.calculationMethod) {
            PrayerCalculationMethod.MUSLIM_WORLD_LEAGUE -> CalculationMethod.MUSLIM_WORLD_LEAGUE.parameters
            PrayerCalculationMethod.EGYPTIAN -> CalculationMethod.EGYPTIAN.parameters
            PrayerCalculationMethod.UMM_AL_QURA -> CalculationMethod.UMM_AL_QURA.parameters
            PrayerCalculationMethod.ISNA -> CalculationMethod.NORTH_AMERICA.parameters
            PrayerCalculationMethod.GULF_REGION -> CalculationMethod.DUBAI.parameters
            PrayerCalculationMethod.FRANCE_MOSQUE -> CalculationParameters(12.0, 12.0, CalculationMethod.OTHER)
        }
        params.madhab = if (settings.asrMadhab == AsrMadhab.HANAFI) Madhab.HANAFI else Madhab.SHAFI
        params.highLatitudeRule = HighLatitudeRule.TWILIGHT_ANGLE
        val offsets = settings.offsets
        params.adjustments = AdhanAdjustments(
            offsets.fajrMinutes,
            offsets.sunriseMinutes,
            offsets.dhuhrMinutes,
            offsets.asrMinutes,
            offsets.maghribMinutes,
            offsets.ishaMinutes,
        )
        return params
    }

    companion object {
        fun anglesFor(method: PrayerCalculationMethod): PrayerAngles? = when (method) {
            PrayerCalculationMethod.MUSLIM_WORLD_LEAGUE -> PrayerAngles(18.0, 17.0)
            PrayerCalculationMethod.EGYPTIAN -> PrayerAngles(19.5, 17.5)
            PrayerCalculationMethod.UMM_AL_QURA -> PrayerAngles(18.5, 0.0)
            PrayerCalculationMethod.ISNA -> PrayerAngles(15.0, 15.0)
            PrayerCalculationMethod.GULF_REGION -> PrayerAngles(18.2, 18.2)
            PrayerCalculationMethod.FRANCE_MOSQUE -> PrayerAngles(12.0, 12.0)
        }
    }
}
