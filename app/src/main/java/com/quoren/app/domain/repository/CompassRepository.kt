package com.quoren.app.domain.repository

import com.quoren.app.domain.qibla.CompassAccuracy
import kotlinx.coroutines.flow.Flow

data class CompassReading(
    val headingDegrees: Double?,
    val accuracy: CompassAccuracy,
)

interface CompassRepository {
    fun isAvailable(): Boolean
    fun observeHeading(latitude: Double, longitude: Double, altitudeMeters: Double = 0.0): Flow<CompassReading>
}