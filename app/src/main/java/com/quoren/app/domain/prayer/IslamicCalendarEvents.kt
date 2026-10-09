package com.quoren.app.domain.prayer

import java.time.LocalDate
import java.time.YearMonth
import java.time.chrono.HijrahChronology
import java.time.temporal.ChronoField

enum class IslamicEventType {
    HIJRI_NEW_YEAR,
    ASHURA,
    MAWLID,
    ISRA_MIRAJ,
    MID_SHAABAN,
    RAMADAN_START,
    EID_FITR,
    ARAFAH,
    EID_ADHA,
}

data class IslamicCalendarEvent(
    val date: LocalDate,
    val hijriYear: Int,
    val hijriMonth: Int,
    val hijriDay: Int,
    val type: IslamicEventType,
)

class IslamicCalendarEventCalculator {

    fun eventsIn(month: YearMonth): List<IslamicCalendarEvent> {
        val chronology = HijrahChronology.INSTANCE
        val firstYear = runCatching {
            chronology.date(month.atDay(1)).get(ChronoField.YEAR_OF_ERA)
        }.getOrNull() ?: return emptyList()
        val lastYear = runCatching {
            chronology.date(month.atEndOfMonth()).get(ChronoField.YEAR_OF_ERA)
        }.getOrNull() ?: return emptyList()

        return (firstYear..lastYear).flatMap { year ->
            EVENT_DATES.mapNotNull { (type, hijriMonth, hijriDay) ->
                runCatching { LocalDate.from(chronology.date(year, hijriMonth, hijriDay)) }
                    .getOrNull()
                    ?.takeIf { YearMonth.from(it) == month }
                    ?.let { IslamicCalendarEvent(it, year, hijriMonth, hijriDay, type) }
            }
        }.distinctBy { it.date to it.type }.sortedBy { it.date }
    }

    private companion object {
        val EVENT_DATES = listOf(
            Triple(IslamicEventType.HIJRI_NEW_YEAR, 1, 1),
            Triple(IslamicEventType.ASHURA, 1, 10),
            Triple(IslamicEventType.MAWLID, 3, 12),
            Triple(IslamicEventType.ISRA_MIRAJ, 7, 27),
            Triple(IslamicEventType.MID_SHAABAN, 8, 15),
            Triple(IslamicEventType.RAMADAN_START, 9, 1),
            Triple(IslamicEventType.EID_FITR, 10, 1),
            Triple(IslamicEventType.ARAFAH, 12, 9),
            Triple(IslamicEventType.EID_ADHA, 12, 10),
        )
    }
}