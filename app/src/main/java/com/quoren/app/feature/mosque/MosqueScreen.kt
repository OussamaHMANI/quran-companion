package com.quoren.app.feature.mosque

import android.Manifest
import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.preference.PreferenceManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.JavascriptInterface
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.viewinterop.AndroidView
import com.quoren.app.R
import com.quoren.app.domain.model.Mosque
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MosqueScreen(
    onBack: () -> Unit,
    onOpenPrayerSettings: () -> Unit,
    viewModel: MosqueViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val mapHeight = (configuration.screenHeightDp * 0.18f).coerceIn(120f, 160f).dp
    var mosqueQuery by remember { mutableStateOf("") }
    var mapVisible by remember { mutableStateOf(true) }
    var showFavorites by remember { mutableStateOf(false) }
    var selectedMosque by remember { mutableStateOf<Mosque?>(null) }
    var showLocationPurpose by remember { mutableStateOf(false) }
    var mapRetryToken by remember { mutableIntStateOf(0) }
    val locationPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        if (result.values.any { it }) viewModel.useCurrentLocation() else viewModel.onLocationPermissionDenied()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.mosque_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::refresh) {
                        Icon(Icons.Filled.LocationOn, contentDescription = stringResource(R.string.mosque_refresh))
                    }
                },
            )
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            state.location?.let { location ->
                Text(
                    stringResource(R.string.mosque_near_location, location.city, location.country),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            OutlinedTextField(
                value = state.cityQuery,
                onValueChange = viewModel::searchCity,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                label = { Text(stringResource(R.string.mosque_search_city)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            )
            if (state.searchingCities) LinearProgressIndicatorMosque()
            state.citySuggestions.take(5).forEach { suggestion ->
                TextButton(
                    onClick = { viewModel.selectCity(suggestion) },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                ) {
                    Text(
                        listOf(suggestion.location.city, suggestion.location.region, suggestion.location.country)
                            .filter(String::isNotBlank).distinct().joinToString("، "),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            if (state.location == null) {
                Text(stringResource(R.string.mosque_location_prompt), modifier = Modifier.padding(top = 8.dp))
                Button(
                    onClick = {
                        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        if (hasFine || hasCoarse) viewModel.useCurrentLocation() else showLocationPurpose = true
                    },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                ) { Text(stringResource(R.string.mosque_use_location)) }
                OutlinedButton(onClick = onOpenPrayerSettings, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                    Text(stringResource(R.string.mosque_open_manual_location))
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 8.dp),
                ) {
                    listOf(1_000, 5_000, 10_000, 20_000).forEach { radius ->
                        item {
                            FilterChip(
                                selected = state.radiusMeters == radius,
                                onClick = { viewModel.setRadius(radius) },
                                label = { Text(if (radius < 1_000) "${radius}m" else "${radius / 1000}km") },
                            )
                        }
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = mapVisible, onClick = { mapVisible = true }, label = { Text(stringResource(R.string.mosque_map)) })
                    FilterChip(selected = !mapVisible, onClick = { mapVisible = false }, label = { Text(stringResource(R.string.mosque_list)) })
                    FilterChip(selected = showFavorites, onClick = { showFavorites = !showFavorites; viewModel.setFavoritesOnly(!showFavorites) }, label = { Text(stringResource(R.string.mosque_favorites)) })
                }
                val visible = viewModel.visibleMosques()
                OutlinedTextField(
                    value = mosqueQuery,
                    onValueChange = {
                        mosqueQuery = it
                        viewModel.setSearchQuery(it)
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    label = { Text(stringResource(R.string.mosque_search_name)) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    singleLine = true,
                )
                if (mapVisible) {
                    val location = checkNotNull(state.location)
                    Spacer(Modifier.height(10.dp))
                    MosqueMapView(
                        location = location,
                        mosques = visible,
                        radiusMeters = state.radiusMeters,
                        retryToken = mapRetryToken,
                        onMosqueSelected = { selectedMosque = it },
                        modifier = Modifier.fillMaxWidth().height(mapHeight).clipToBounds(),
                    )
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            stringResource(R.string.prayer_osm_attribution),
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.labelSmall,
                        )
                        TextButton(onClick = { mapRetryToken++ }) {
                            Text(stringResource(R.string.mosque_map_retry))
                        }
                        TextButton(onClick = {
                            openGoogleMaps(context, location.latitude, location.longitude)
                        }) { Text(stringResource(R.string.mosque_open_google_maps)) }
                    }
                }
                if (state.loading) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.Center) { CircularProgressIndicator() }
                }
                state.error?.let { Text(stringResource(R.string.mosque_search_error), color = MaterialTheme.colorScheme.error) }
                if (visible.isEmpty() && !state.loading) {
                    Text(stringResource(if (showFavorites) R.string.mosque_no_favorites else R.string.mosque_no_results), modifier = Modifier.padding(12.dp))
                    if (!showFavorites) OutlinedButton(onClick = viewModel::refresh) { Text(stringResource(R.string.action_retry)) }
                }
                LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(visible, key = { it.id }) { mosque ->
                        MosqueRow(
                            mosque = mosque,
                            onOpen = { selectedMosque = mosque },
                            onToggleFavorite = { viewModel.toggleFavorite(mosque) },
                            onDirections = { openDirections(context, mosque) },
                        )
                    }
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

    selectedMosque?.let { mosque ->
        ModalBottomSheet(onDismissRequest = { selectedMosque = null }) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(mosque.name, style = MaterialTheme.typography.titleLarge)
                mosque.address?.let { Text(it) }
                Text(formatMosqueDistance(mosque.distanceMeters), color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(stringResource(R.string.mosque_coordinates, mosque.latitude, mosque.longitude), style = MaterialTheme.typography.labelSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { openDirections(context, mosque) }, modifier = Modifier.weight(1f).heightIn(min = 48.dp)) {
                        Text(stringResource(R.string.mosque_directions))
                    }
                    OutlinedButton(onClick = { viewModel.toggleFavorite(mosque); selectedMosque = null }, modifier = Modifier.weight(1f).heightIn(min = 48.dp)) {
                        Text(stringResource(if (mosque.isFavorite) R.string.mosque_remove_favorite else R.string.mosque_add_favorite))
                    }
                }
                mosque.phone?.let { phone ->
                    TextButton(onClick = { openDialer(context, phone) }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                        Text(stringResource(R.string.mosque_call))
                    }
                }
                mosque.website?.takeIf(::isSafeWebUrl)?.let { website ->
                    TextButton(onClick = { openWebPage(context, website) }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                        Text(stringResource(R.string.mosque_website))
                    }
                }
            }
        }
    }
}

@Composable
private fun LinearProgressIndicatorMosque() {
    androidx.compose.material3.LinearProgressIndicator(Modifier.fillMaxWidth())
}

@Composable
private fun MosqueRow(
    mosque: Mosque,
    onOpen: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDirections: () -> Unit,
) {
    Card(
        onClick = onOpen,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(mosque.name, style = MaterialTheme.typography.titleMedium)
            mosque.address?.let { Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall) }
            Text(formatMosqueDistance(mosque.distanceMeters), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onDirections, modifier = Modifier.heightIn(min = 44.dp)) { Text(stringResource(R.string.mosque_directions)) }
                TextButton(onClick = onToggleFavorite, modifier = Modifier.heightIn(min = 44.dp)) {
                    Text(stringResource(if (mosque.isFavorite) R.string.mosque_remove_favorite else R.string.mosque_add_favorite))
                }
            }
        }
    }
}

@Composable
private fun MosqueMapView(
    location: com.quoren.app.domain.model.PrayerLocation,
    mosques: List<Mosque>,
    radiusMeters: Int,
    retryToken: Int,
    onMosqueSelected: (Mosque) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val selectMosque by rememberUpdatedState(onMosqueSelected)
    val mapView = remember(context, retryToken) {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        Configuration.getInstance().userAgentValue = "com.quoren.app/0.1 (Android)"
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setUseDataConnection(true)
            setMultiTouchControls(true)
            setTilesScaledToDpi(true)
            overlays.add(CopyrightOverlay(context))
            overlays.add(
                MapEventsOverlay(object : MapEventsReceiver {
                    override fun singleTapConfirmedHelper(point: GeoPoint): Boolean {
                        openGoogleMaps(context, point.latitude, point.longitude)
                        return true
                    }

                    override fun longPressHelper(point: GeoPoint): Boolean = false
                }),
            )
        }
    }

    LaunchedEffect(location.latitude, location.longitude, radiusMeters) {
        mapView.controller.setZoom(zoomForRadius(radiusMeters))
        mapView.controller.setCenter(GeoPoint(location.latitude, location.longitude))
    }

    DisposableEffect(mapView, lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onPause()
            mapView.onDetach()
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier,
        update = { nativeMap ->
            nativeMap.overlays.removeAll { it is Marker }
            nativeMap.overlays.add(
                Marker(nativeMap).apply {
                    position = GeoPoint(location.latitude, location.longitude)
                    title = location.city
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                },
            )
            mosques.forEach { mosque ->
                nativeMap.overlays.add(
                    Marker(nativeMap).apply {
                        position = GeoPoint(mosque.latitude, mosque.longitude)
                        title = mosque.name
                        snippet = mosque.address
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        setOnMarkerClickListener { _, _ ->
                            selectMosque(mosque)
                            true
                        }
                    },
                )
            }
            nativeMap.invalidate()
        },
    )
}

private fun zoomForRadius(radiusMeters: Int): Double = when {
    radiusMeters >= 20_000 -> 9.0
    radiusMeters >= 10_000 -> 10.0
    radiusMeters >= 5_000 -> 12.0
    else -> 14.0
}

private fun openDirections(context: android.content.Context, mosque: Mosque) {
    val coordinates = "${mosque.latitude},${mosque.longitude}"
    openGoogleMaps(context, "https://www.google.com/maps/dir/?api=1&destination=${Uri.encode(coordinates)}")
}

private fun openGoogleMaps(context: android.content.Context, latitude: Double, longitude: Double) {
    val coordinates = "${latitude},${longitude}"
    openGoogleMaps(context, "https://www.google.com/maps/search/?api=1&query=${Uri.encode(coordinates)}")
}

private fun openGoogleMaps(context: android.content.Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).setPackage(GOOGLE_MAPS_PACKAGE)
    if (runCatching { context.startActivity(intent) }.isFailure) openWebPage(context, url)
}

private fun openDialer(context: android.content.Context, rawPhone: String) {
    val number = rawPhone.filter { it.isDigit() || it == '+' }
    if (number.isNotBlank()) runCatching { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))) }
}

private fun openWebPage(context: android.content.Context, url: String) {
    if (isSafeWebUrl(url)) runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
}

private fun isSafeWebUrl(value: String): Boolean = runCatching {
    val uri = Uri.parse(value)
    uri.scheme in setOf("http", "https") && !uri.host.isNullOrBlank()
}.getOrDefault(false)

private fun formatMosqueDistance(meters: Double): String = if (meters < 1_000) {
    String.format(java.util.Locale.ROOT, "%.0f m", meters)
} else {
    String.format(java.util.Locale.ROOT, "%.1f km", meters / 1_000.0)
}

private const val GOOGLE_MAPS_PACKAGE = "com.google.android.apps.maps"

