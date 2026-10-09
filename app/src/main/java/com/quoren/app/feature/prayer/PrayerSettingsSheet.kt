package com.quoren.app.feature.prayer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.quoren.app.R
import com.quoren.app.domain.model.AsrMadhab
import com.quoren.app.domain.model.AdhanSound
import com.quoren.app.domain.model.PrayerCalculationMethod
import com.quoren.app.domain.model.PrayerLocation
import com.quoren.app.domain.model.PrayerCitySuggestion as ResolvedCitySuggestion
import com.quoren.app.domain.model.PrayerLandmark
import com.quoren.app.domain.model.PrayerOffsets
import com.quoren.app.domain.model.PrayerSettings
import java.time.ZoneId
import kotlinx.coroutines.delay

@Composable
fun PrayerSettingsSheet(
    location: PrayerLocation?,
    settings: PrayerSettings,
    citySuggestions: List<ResolvedCitySuggestion>,
    isSearchingCities: Boolean,
    isSearchingMainMosque: Boolean,
    mainMosqueSuggestion: PrayerLandmark?,
    onDismiss: () -> Unit,
    onSave: (PrayerLocation, PrayerSettings) -> Unit,
    onRequestDeviceLocation: () -> Unit,
    onSearchCities: (String) -> Unit,
    onFindMainMosque: (PrayerLocation) -> Unit,
    onPreviewAdhan: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    var city by remember(location) { mutableStateOf(location?.city.orEmpty()) }
    var region by remember(location) { mutableStateOf(location?.region.orEmpty()) }
    var country by remember(location) { mutableStateOf(location?.country.orEmpty()) }
    var mosqueName by remember(location) { mutableStateOf(location?.mosqueName) }
    var latitude by remember(location) { mutableStateOf(location?.latitude?.toString().orEmpty()) }
    var longitude by remember(location) { mutableStateOf(location?.longitude?.toString().orEmpty()) }
    var timeZone by remember(location) { mutableStateOf(location?.timeZoneId ?: ZoneId.systemDefault().id) }
    var method by remember(settings) { mutableStateOf(settings.calculationMethod) }
    var madhab by remember(settings) { mutableStateOf(settings.asrMadhab) }
    var notificationsEnabled by remember(settings) { mutableStateOf(settings.notificationsEnabled) }
    var adhanEnabled by remember(settings) { mutableStateOf(settings.adhanEnabled) }
    var adhanSound by remember(settings) { mutableStateOf(settings.adhanSound) }
    var reminderMinutes by remember(settings) { mutableStateOf(settings.reminderMinutesBefore) }
    val offsets = remember(settings) {
        mutableStateListOf(
            settings.offsets.fajrMinutes.toString(),
            settings.offsets.sunriseMinutes.toString(),
            settings.offsets.dhuhrMinutes.toString(),
            settings.offsets.asrMinutes.toString(),
            settings.offsets.maghribMinutes.toString(),
            settings.offsets.ishaMinutes.toString(),
        )
    }
    var methodsExpanded by remember { mutableStateOf(false) }
    LaunchedEffect(city) {
        delay(CITY_SEARCH_DEBOUNCE_MS)
        onSearchCities(city)
    }
    val lat = latitude.toDoubleOrNull()
    val lon = longitude.toDoubleOrNull()
    val valid = city.isNotBlank() && lat != null && lat in -90.0..90.0 &&
        lon != null && lon in -180.0..180.0 && runCatching { ZoneId.of(timeZone) }.isSuccess

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.prayer_settings)) },
        text = {
            Column(
                Modifier.fillMaxWidth().heightIn(max = 560.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(onClick = onRequestDeviceLocation, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                    Text(stringResource(R.string.prayer_use_device_location))
                }
                OutlinedTextField(city, { city = it; mosqueName = null }, label = { Text(stringResource(R.string.prayer_city)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                if (isSearchingCities) LinearProgressIndicator(Modifier.fillMaxWidth())
                val localSuggestions = PrayerCitySuggestions.search(city).map { local ->
                    ResolvedCitySuggestion(
                        location = local.location,
                        mainMosque = PrayerLandmark(
                            local.location.mosqueName.orEmpty(),
                            local.mosqueLatitude,
                            local.mosqueLongitude,
                        ),
                    )
                }.filter { it.mainMosque?.name?.isNotBlank() == true }
                (localSuggestions + citySuggestions)
                    .distinctBy { "${it.location.city}|${it.location.region}|${it.location.country}" }
                    .take(5)
                    .forEach { suggestion ->
                    OutlinedButton(
                        onClick = {
                            val selected = suggestion.location
                            city = selected.city
                            region = selected.region
                            country = selected.country
                            latitude = suggestion.mainMosque?.latitude?.toString() ?: selected.latitude.toString()
                            longitude = suggestion.mainMosque?.longitude?.toString() ?: selected.longitude.toString()
                            timeZone = selected.timeZoneId
                            mosqueName = suggestion.mainMosque?.name ?: selected.mosqueName
                            if (suggestion.mainMosque == null) onFindMainMosque(selected)
                        },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp),
                    ) {
                        Column {
                            Text(listOf(suggestion.location.city, suggestion.location.region, suggestion.location.country).filter(String::isNotBlank).distinct().joinToString("، "))
                            suggestion.mainMosque?.let { Text(stringResource(R.string.prayer_main_mosque, it.name)) }
                            if (suggestion.location.city.equals("Sfax", ignoreCase = true) && suggestion.mainMosque != null) {
                                Text(stringResource(R.string.prayer_osm_attribution), style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
                if (isSearchingMainMosque) LinearProgressIndicator(Modifier.fillMaxWidth())
                mainMosqueSuggestion?.let { mosque ->
                    OutlinedButton(
                        onClick = {
                            latitude = mosque.latitude.toString()
                            longitude = mosque.longitude.toString()
                            mosqueName = mosque.name
                        },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                    ) {
                        Column {
                            Text(stringResource(R.string.prayer_use_main_mosque, mosque.name))
                            Text(stringResource(R.string.prayer_keep_city_center), style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                if (citySuggestions.isNotEmpty() && !isSearchingCities) {
                    Text(stringResource(R.string.prayer_geocoder_attribution), style = MaterialTheme.typography.labelSmall)
                }
                OutlinedTextField(region, { region = it }, label = { Text(stringResource(R.string.prayer_region)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(country, { country = it }, label = { Text(stringResource(R.string.prayer_country)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        latitude,
                        { latitude = it.filter { char -> char.isDigit() || char == '-' || char == '.' }.take(12) },
                        label = { Text(stringResource(R.string.prayer_latitude)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                    )
                    OutlinedTextField(
                        longitude,
                        { longitude = it.filter { char -> char.isDigit() || char == '-' || char == '.' }.take(12) },
                        label = { Text(stringResource(R.string.prayer_longitude)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                    )
                }
                OutlinedTextField(timeZone, { timeZone = it }, label = { Text(stringResource(R.string.prayer_timezone)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Text(stringResource(R.string.prayer_method), style = MaterialTheme.typography.titleSmall)
                Column {
                    OutlinedButton(onClick = { methodsExpanded = true }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                        Text(stringResource(methodLabel(method)))
                    }
                    DropdownMenu(expanded = methodsExpanded, onDismissRequest = { methodsExpanded = false }) {
                        PrayerCalculationMethod.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(stringResource(methodLabel(option))) },
                                onClick = { method = option; methodsExpanded = false },
                            )
                        }
                    }
                }
                Text(stringResource(R.string.prayer_asr_madhab), style = MaterialTheme.typography.titleSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = madhab == AsrMadhab.SHAFI, onClick = { madhab = AsrMadhab.SHAFI }, label = { Text(stringResource(R.string.prayer_shafi)) })
                    FilterChip(selected = madhab == AsrMadhab.HANAFI, onClick = { madhab = AsrMadhab.HANAFI }, label = { Text(stringResource(R.string.prayer_hanafi)) })
                }
                Text(stringResource(R.string.prayer_offsets), style = MaterialTheme.typography.titleSmall)
                val offsetLabels = listOf(
                    R.string.prayer_fajr, R.string.prayer_sunrise, R.string.prayer_dhuhr,
                    R.string.prayer_asr, R.string.prayer_maghrib, R.string.prayer_isha,
                )
                repeat(3) { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        repeat(2) { column ->
                            val index = row * 2 + column
                            OutlinedTextField(
                                offsets[index],
                                { offsets[index] = it.filter { char -> char.isDigit() || char == '-' }.take(3) },
                                label = { Text(stringResource(offsetLabels[index])) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Text(stringResource(R.string.prayer_notifications), modifier = Modifier.weight(1f))
                    Switch(checked = notificationsEnabled, onCheckedChange = { notificationsEnabled = it })
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Text(stringResource(R.string.prayer_play_adhan), modifier = Modifier.weight(1f))
                    Switch(
                        checked = adhanEnabled,
                        onCheckedChange = { enabled ->
                            adhanEnabled = enabled
                            if (enabled) {
                                notificationsEnabled = true
                                adhanSound = AdhanSound.OTHER
                            }
                        },
                    )
                }
                if (adhanEnabled) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = adhanSound == AdhanSound.OTHER,
                            onClick = { adhanSound = AdhanSound.OTHER },
                            label = { Text(stringResource(R.string.prayer_adhan_other)) },
                        )
                        FilterChip(
                            selected = adhanSound == AdhanSound.NOTIFICATION_ONLY,
                            onClick = { adhanSound = AdhanSound.NOTIFICATION_ONLY; adhanEnabled = false },
                            label = { Text(stringResource(R.string.prayer_adhan_notification_only)) },
                        )
                    }
                    OutlinedButton(onClick = onPreviewAdhan, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                        Text(stringResource(R.string.prayer_adhan_preview))
                    }
                    if (adhanSound == AdhanSound.OTHER) {
                        Text(stringResource(R.string.prayer_adhan_credit_short), style = MaterialTheme.typography.labelSmall)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(onClick = { uriHandler.openUri(ADHAN_SOURCE_URL) }) {
                                Text(stringResource(R.string.prayer_adhan_source_link))
                            }
                            TextButton(onClick = { uriHandler.openUri(ADHAN_LICENSE_URL) }) {
                                Text(stringResource(R.string.prayer_adhan_license_link))
                            }
                        }
                    }
                }
                Text(stringResource(R.string.prayer_reminder), style = MaterialTheme.typography.titleSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(0, 5, 10, 15, 30).forEach { minutes ->
                        FilterChip(
                            selected = reminderMinutes == minutes,
                            onClick = { reminderMinutes = minutes; if (minutes > 0) notificationsEnabled = true },
                            label = { Text(if (minutes == 0) stringResource(R.string.prayer_reminder_at_time) else "$minutes") },
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val prayerLocation = PrayerLocation(
                        latitude = checkNotNull(lat),
                        longitude = checkNotNull(lon),
                        city = city.trim(),
                        region = region.trim(),
                        country = country.trim(),
                        timeZoneId = timeZone.trim(),
                        mosqueName = mosqueName,
                    )
                    val values = offsets.map { it.toIntOrNull()?.coerceIn(-60, 60) ?: 0 }
                    val prayerSettings = settings.copy(
                        calculationMethod = method,
                        asrMadhab = madhab,
                        offsets = PrayerOffsets(values[0], values[1], values[2], values[3], values[4], values[5]),
                        notificationsEnabled = notificationsEnabled,
                        reminderMinutesBefore = reminderMinutes,
                        adhanEnabled = adhanEnabled,
                        adhanSound = adhanSound,
                    )
                    onSave(prayerLocation, prayerSettings)
                },
                enabled = valid,
            ) { Text(stringResource(R.string.prayer_save)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_close)) } },
    )
}

private fun methodLabel(method: PrayerCalculationMethod): Int = when (method) {
    PrayerCalculationMethod.MUSLIM_WORLD_LEAGUE -> R.string.prayer_method_mwl
    PrayerCalculationMethod.EGYPTIAN -> R.string.prayer_method_egyptian
    PrayerCalculationMethod.UMM_AL_QURA -> R.string.prayer_method_umm_al_qura
    PrayerCalculationMethod.ISNA -> R.string.prayer_method_isna
    PrayerCalculationMethod.GULF_REGION -> R.string.prayer_method_gulf
    PrayerCalculationMethod.FRANCE_MOSQUE -> R.string.prayer_method_france
}

private const val ADHAN_SOURCE_URL = "https://commons.wikimedia.org/wiki/File:The_Adhan_-_Muslim_Call_to_Prayer_-_Aaqib_Azeez.mp3"
private const val ADHAN_LICENSE_URL = "https://creativecommons.org/licenses/by-sa/4.0/"
private const val CITY_SEARCH_DEBOUNCE_MS = 350L