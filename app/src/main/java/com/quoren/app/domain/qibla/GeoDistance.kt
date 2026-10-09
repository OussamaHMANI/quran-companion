package com.quoren.app.domain.qibla

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object GeoDistance {
    fun meters(latitude1: Double, longitude1: Double, latitude2: Double, longitude2: Double): Double {
        require(latitude1 in -90.0..90.0 && latitude2 in -90.0..90.0)
        require(longitude1 in -180.0..180.0 && longitude2 in -180.0..180.0)
        val latitudeDelta = Math.toRadians(latitude2 - latitude1)
        val longitudeDelta = Math.toRadians(longitude2 - longitude1)
        val latitude1Radians = Math.toRadians(latitude1)
        val latitude2Radians = Math.toRadians(latitude2)
        val haversine = sin(latitudeDelta / 2).let { it * it } +
            cos(latitude1Radians) * cos(latitude2Radians) * sin(longitudeDelta / 2).let { it * it }
        return QiblaEngine.EARTH_RADIUS_METERS * 2.0 *
            atan2(sqrt(haversine.coerceIn(0.0, 1.0)), sqrt((1.0 - haversine).coerceIn(0.0, 1.0)))
    }
}