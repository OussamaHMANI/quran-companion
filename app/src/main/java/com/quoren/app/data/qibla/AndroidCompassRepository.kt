package com.quoren.app.data.qibla

import android.content.Context
import android.hardware.GeomagneticField
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.view.Surface
import android.view.WindowManager
import com.quoren.app.domain.qibla.CompassAccuracy
import com.quoren.app.domain.qibla.CompassHeadingSmoother
import com.quoren.app.domain.repository.CompassReading
import com.quoren.app.domain.repository.CompassRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@Singleton
class AndroidCompassRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) : CompassRepository {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    override fun isAvailable(): Boolean = accelerometer != null && magnetometer != null

    override fun observeHeading(latitude: Double, longitude: Double, altitudeMeters: Double): Flow<CompassReading> = callbackFlow {
        if (!isAvailable()) {
            trySend(CompassReading(null, CompassAccuracy.UNAVAILABLE))
            close()
            return@callbackFlow
        }

        val gravity = FloatArray(3)
        val magnetic = FloatArray(3)
        val rotation = FloatArray(9)
        val remappedRotation = FloatArray(9)
        val orientation = FloatArray(3)
        val smoother = CompassHeadingSmoother()
        var hasGravity = false
        var hasMagnetic = false
        var magneticAccuracy = SensorManager.SENSOR_STATUS_UNRELIABLE
        val displayRotation = displayRotation()
        val (axisX, axisY) = axesFor(displayRotation)
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        event.values.copyInto(gravity)
                        hasGravity = true
                    }
                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        event.values.copyInto(magnetic)
                        hasMagnetic = true
                    }
                }
                if (!hasGravity || !hasMagnetic ||
                    !SensorManager.getRotationMatrix(rotation, null, gravity, magnetic) ||
                    !SensorManager.remapCoordinateSystem(rotation, axisX, axisY, remappedRotation)
                ) return

                SensorManager.getOrientation(remappedRotation, orientation)
                val geomagnetic = GeomagneticField(
                    latitude.toFloat(),
                    longitude.toFloat(),
                    altitudeMeters.toFloat(),
                    System.currentTimeMillis(),
                )
                val magneticNorthHeading = Math.toDegrees(orientation[0].toDouble())
                val trueNorthHeading = magneticNorthHeading + geomagnetic.declination
                trySend(
                    CompassReading(
                        headingDegrees = smoother.smooth(trueNorthHeading),
                        accuracy = accuracyFor(magneticAccuracy),
                    ),
                )
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                if (sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) magneticAccuracy = accuracy
            }
        }

        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(listener, magnetometer, SensorManager.SENSOR_DELAY_UI)
        awaitClose { sensorManager.unregisterListener(listener) }
    }

    @Suppress("DEPRECATION")
    private fun displayRotation(): Int = if (Build.VERSION.SDK_INT >= 30) {
        context.display?.rotation ?: Surface.ROTATION_0
    } else {
        (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.rotation
    }

    private fun axesFor(rotation: Int): Pair<Int, Int> = when (rotation) {
        Surface.ROTATION_90 -> SensorManager.AXIS_Y to SensorManager.AXIS_MINUS_X
        Surface.ROTATION_180 -> SensorManager.AXIS_MINUS_X to SensorManager.AXIS_MINUS_Y
        Surface.ROTATION_270 -> SensorManager.AXIS_MINUS_Y to SensorManager.AXIS_X
        else -> SensorManager.AXIS_X to SensorManager.AXIS_Y
    }

    private fun accuracyFor(status: Int): CompassAccuracy = when (status) {
        SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> CompassAccuracy.HIGH
        SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> CompassAccuracy.MEDIUM
        SensorManager.SENSOR_STATUS_ACCURACY_LOW -> CompassAccuracy.LOW
        else -> CompassAccuracy.UNAVAILABLE
    }
}