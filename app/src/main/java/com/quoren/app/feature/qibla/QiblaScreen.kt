package com.quoren.app.feature.qibla

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.quoren.app.R
import com.quoren.app.domain.qibla.CompassAccuracy
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiblaScreen(
    onBack: () -> Unit,
    onOpenPrayerSettings: () -> Unit,
    viewModel: QiblaViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var showLocationPurpose by remember { mutableStateOf(false) }
    val locationPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        if (result.values.any { it }) viewModel.useCurrentLocation() else viewModel.onLocationPermissionDenied()
    }

    DisposableEffect(lifecycleOwner, state.location?.latitude, state.location?.longitude, state.compassAvailable) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> viewModel.startCompass()
                Lifecycle.Event.ON_STOP -> viewModel.stopCompass()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) viewModel.startCompass()
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.stopCompass()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.qibla_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                },
            )
        },
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                state.location?.let { listOf(it.city, it.country).filter(String::isNotBlank).joinToString("، ") }
                    ?: stringResource(R.string.qibla_location_missing),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (state.isLocating) {
                CircularProgressIndicator()
            } else if (state.location == null) {
                Text(stringResource(if (state.locationUnavailable) R.string.qibla_location_unavailable else R.string.qibla_location_prompt), textAlign = TextAlign.Center)
                Button(
                    onClick = {
                        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        if (fine || coarse) viewModel.useCurrentLocation() else showLocationPurpose = true
                    },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                ) { Text(stringResource(R.string.qibla_use_location)) }
                OutlinedButton(onClick = onOpenPrayerSettings, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                    Text(stringResource(R.string.qibla_open_prayer_settings))
                }
            } else {
                val direction = state.direction
                if (direction != null) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(300.dp)) {
                        CompassFace(modifier = Modifier.fillMaxSize())
                        val rotation = (state.relativeBearingDegrees ?: direction.bearingDegrees).toFloat()
                        val qiblaColor = MaterialTheme.colorScheme.primary
                        Canvas(Modifier.fillMaxSize().padding(35.dp).rotate(rotation)) {
                            val center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f)
                            val tip = androidx.compose.ui.geometry.Offset(center.x, size.height * 0.08f)
                            drawLine(
                                color = qiblaColor,
                                start = center,
                                end = tip,
                                strokeWidth = 9.dp.toPx(),
                                cap = StrokeCap.Round,
                            )
                            val arrow = androidx.compose.ui.graphics.Path().apply {
                                moveTo(tip.x, tip.y - 14.dp.toPx())
                                lineTo(tip.x - 12.dp.toPx(), tip.y + 12.dp.toPx())
                                lineTo(tip.x + 12.dp.toPx(), tip.y + 12.dp.toPx())
                                close()
                            }
                            drawPath(arrow, qiblaColor)
                            drawCircle(qiblaColor, radius = 10.dp.toPx(), center = center)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(stringResource(R.string.qibla_kaaba), style = MaterialTheme.typography.titleMedium)
                            Text("${direction.bearingDegrees.toInt()}°", style = MaterialTheme.typography.titleLarge)
                        }
                    }
                    Text(stringResource(R.string.qibla_distance, formatDistance(direction.distanceMeters)), style = MaterialTheme.typography.titleMedium)
                    state.headingDegrees?.let { Text(stringResource(R.string.qibla_heading, it.toInt())) }
                    AccuracyStatus(state.accuracy)
                    if (state.accuracy == CompassAccuracy.LOW || state.accuracy == CompassAccuracy.UNAVAILABLE) {
                        Text(stringResource(R.string.qibla_calibration_help), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.error)
                    }
                } else {
                    Text(stringResource(R.string.qibla_calculation_error), color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }

    if (showLocationPurpose) {
        AlertDialog(
            onDismissRequest = { showLocationPurpose = false },
            title = { Text(stringResource(R.string.prayer_location_permission_title)) },
            text = { Text(stringResource(R.string.prayer_location_permission_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showLocationPurpose = false
                    locationPermission.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION))
                }) { Text(stringResource(R.string.action_continue)) }
            },
            dismissButton = { TextButton(onClick = { showLocationPurpose = false }) { Text(stringResource(R.string.action_close)) } },
        )
    }
}

@Composable
private fun CompassFace(modifier: Modifier = Modifier) {
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val outline = MaterialTheme.colorScheme.outline
    val primary = MaterialTheme.colorScheme.primary
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant
    Canvas(modifier) {
        val center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension * 0.47f
        drawCircle(surfaceVariant, radius, center)
        drawCircle(outline, radius, center, style = Stroke(2.dp.toPx()))
        repeat(36) { tick ->
            val angle = Math.toRadians(tick * 10.0 - 90.0)
            val major = tick % 3 == 0
            val outer = radius * 0.94f
            val inner = radius * if (major) 0.82f else 0.88f
            drawLine(
                color = if (major) primary else outlineVariant,
                start = androidx.compose.ui.geometry.Offset(center.x + cos(angle).toFloat() * inner, center.y + sin(angle).toFloat() * inner),
                end = androidx.compose.ui.geometry.Offset(center.x + cos(angle).toFloat() * outer, center.y + sin(angle).toFloat() * outer),
                strokeWidth = if (major) 2.dp.toPx() else 1.dp.toPx(),
            )
        }
    }
}

@Composable
private fun AccuracyStatus(accuracy: CompassAccuracy) {
    val label = when (accuracy) {
        CompassAccuracy.HIGH -> R.string.qibla_accuracy_high
        CompassAccuracy.MEDIUM -> R.string.qibla_accuracy_medium
        CompassAccuracy.LOW -> R.string.qibla_accuracy_low
        CompassAccuracy.UNAVAILABLE -> R.string.qibla_sensor_unavailable
    }
    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.secondaryContainer) {
        Text(stringResource(label), modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), style = MaterialTheme.typography.labelLarge)
    }
}

private fun formatDistance(meters: Double): String = if (meters < 1_000) {
    String.format(java.util.Locale.ROOT, "%.0f m", meters)
} else {
    String.format(java.util.Locale.ROOT, "%.0f km", meters / 1_000.0)
}
