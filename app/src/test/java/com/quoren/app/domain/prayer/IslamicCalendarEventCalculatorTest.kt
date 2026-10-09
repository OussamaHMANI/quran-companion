package com.quoren.app.domain.prayer

import java.time.LocalDate
import java.time.YearMonth
import java.time.chrono.HijrahChronology
import org.junit.Assert.assertTrue
import org.junit.Test

class IslamicCalendarEventCalculatorTest {

    private val calculator = IslamicCalendarEventCalculator()

    @Test
    fun allDeclaredEventsAppearInTheirGregorianMonth() {
        val definitions = listOf(
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

        definitions.forEach { (type, month, day) ->
            val date = LocalDate.from(HijrahChronology.INSTANCE.date(1448, month, day))
            val selectedMonth = YearMonth.from(date)
            val events = calculator.eventsIn(selectedMonth)

            assertTrue(events.all { YearMonth.from(it.date) == selectedMonth })
            assertTrue(events.any { it.type == type && it.date == date })
        }
    }
}