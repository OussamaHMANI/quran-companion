package com.quoren.app.domain.qibla

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class QiblaEngineTest {

    private val engine = QiblaEngine()

    @Test
    fun calculatesParisBearingAndDistanceToKaaba() {
        val result = engine.calculate(48.8566, 2.3522)
        assertEquals(120.0, result.bearingDegrees, 1.0)
        assertEquals(4_496_000.0, result.distanceMeters, 10_000.0)
    }

    @Test
    fun qiblaAtKaabaHasZeroDistanceAndStableBearing() {
        val result = engine.calculate(QiblaEngine.KAABA_LATITUDE, QiblaEngine.KAABA_LONGITUDE)
        assertEquals(0.0, result.distanceMeters, 0.01)
        assertTrue(result.bearingDegrees in 0.0..360.0)
    }

    @Test
    fun relativeBearingWrapsAcrossNorth() {
        assertEquals(10.0, engine.relativeBearing(5.0, 355.0), 0.0001)
        assertEquals(350.0, engine.relativeBearing(355.0, 5.0), 0.0001)
    }

    @Test
    fun rejectsInvalidCoordinates() {
        assertThrows(IllegalArgumentException::class.java) { engine.calculate(91.0, 0.0) }
        assertThrows(IllegalArgumentException::class.java) { engine.calculate(0.0, -181.0) }
    }

    @Test
    fun headingSmootherUsesShortestRotationAcrossNorth() {
        val smoother = CompassHeadingSmoother(alpha = 0.5)
        assertEquals(359.0, smoother.smooth(359.0), 0.0001)
        assertEquals(0.0, smoother.smooth(1.0), 0.0001)
        smoother.reset()
        assertEquals(1.0, smoother.smooth(1.0), 0.0001)
    }
}