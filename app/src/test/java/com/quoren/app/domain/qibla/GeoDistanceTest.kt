package com.quoren.app.domain.qibla

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class GeoDistanceTest {
    @Test
    fun calculatesShortDistanceAndZeroDistance() {
        assertEquals(0.0, GeoDistance.meters(34.0, 10.0, 34.0, 10.0), 0.001)
        assertEquals(100.3, GeoDistance.meters(34.7394361, 10.7604024, 34.7394361, 10.7615), 1.0)
    }

    @Test
    fun rejectsInvalidCoordinates() {
        assertThrows(IllegalArgumentException::class.java) { GeoDistance.meters(-91.0, 0.0, 0.0, 0.0) }
    }
}