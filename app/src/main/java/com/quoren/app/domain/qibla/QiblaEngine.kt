package com.quoren.app.domain.qibla

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class QiblaDirection(
    val bearingDegrees: Double,
    val distanceMeters: Double,
)

enum class CompassAccuracy { HIGH, MEDIUM, LOW, UNAVAILABLE }

class CompassHeadingSmoother(private val alpha: Double = 0.18) {
    private var previous: Double? = null

    init {
        require(alpha in 0.0..1.0)
    }

    fun smooth(headingDegrees: Double): Double {
        val normalized = ((headingDegrees % 360.0) + 360.0) % 360.0
        val current = previous
        val result = if (current == null) {
            normalized
        } else {
            val delta = ((normalized - current + 540.0) % 360.0) - 180.0
            ((current + delta * alpha + 360.0) % 360.0)
        }
        previous = result
        return result
    }

    fun reset() {
        previous = null
    }
}

@Singleton
class QiblaEngine @Inject constructor() {

    fun calculate(latitude: Double, longitude: Double): QiblaDirection {
        require(latitude in -90.0..90.0 && longitude in -180.0..180.0)
        val latitudeRadians = Math.toRadians(latitude)
        val kaabaLatitudeRadians = Math.toRadians(KAABA_LATITUDE)
        val longitudeDifference = Math.toRadians(KAABA_LONGITUDE - longitude)

        val y = sin(longitudeDifference)
        val x = cos(latitudeRadians) * kotlin.math.tan(kaabaLatitudeRadians) -
            sin(latitudeRadians) * cos(longitudeDifference)
        val bearing = normalizeDegrees(Math.toDegrees(atan2(y, x)))

        val latitudeDifference = kaabaLatitudeRadians - latitudeRadians
        val haversine = sin(latitudeDifference / 2).let { it * it } +
            cos(latitudeRadians) * cos(kaabaLatitudeRadians) * sin(longitudeDifference / 2).let { it * it }
        val distance = EARTH_RADIUS_METERS * 2.0 * atan2(sqrt(haversine.coerceIn(0.0, 1.0)), sqrt((1.0 - haversine).coerceIn(0.0, 1.0)))
        return QiblaDirection(bearing, distance)
    }

    fun relativeBearing(qiblaBearingDegrees: Double, deviceHeadingDegrees: Double): Double =
        normalizeDegrees(qiblaBearingDegrees - deviceHeadingDegrees)

    fun accuracyForSensorStatus(status: Int): CompassAccuracy = when (status) {
        3 -> CompassAccuracy.HIGH
        2 -> CompassAccuracy.MEDIUM
        1 -> CompassAccuracy.LOW
        else -> CompassAccuracy.UNAVAILABLE
    }

    private fun normalizeDegrees(value: Double): Double = ((value % 360.0) + 360.0) % 360.0

    companion object {
        const val KAABA_LATITUDE = 21.422487
        const val KAABA_LONGITUDE = 39.826206
        const val EARTH_RADIUS_METERS = 6_371_008.8
    }
}