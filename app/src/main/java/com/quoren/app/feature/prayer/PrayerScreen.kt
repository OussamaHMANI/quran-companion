package com.quoren.app.feature.prayer

import android.Manifest
import android.app.AlarmManager
import android.content.pm.PackageManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quoren.app.R
import com.quoren.app.domain.model.PrayerName
import com.quoren.app.domain.model.PrayerLocation
import com.quoren.app.domain.model.PrayerSettings
import com.quoren.app.domain.prayer.IslamicCalendarEvent
import com.quoren.app.domain.prayer.IslamicCalendarEventCalculator
import com.quoren.app.domain.prayer.IslamicEventType
import java.time.ZoneId
import java.time.YearMonth
import java.time.chrono.HijrahChronology
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerScreen(
    onBack: () -> Unit,
    viewModel: PrayerViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showSettings by remember { mutableStateOf(false) }
    var showWeek by remember { mutableStateOf(false) }
    var showMonth by remember { mutableStateOf(false) }
    var showLocationPurpose by remember { mutableStateOf(false) }
    var showNotificationPurpose by remember { mutableStateOf(false) }
    var showExactAlarmPurpose by remember { mutableStateOf(false) }
    var pendingNotificationSave by remember { mutableStateOf<Pair<PrayerLocation, PrayerSettings>?>(null) }
    var pendingExactAlarmSave by remember { mutableStateOf<Pair<PrayerLocation, PrayerSettings>?>(null) }
    var previewAfterPermission by remember { mutableStateOf(false) }
    val context = LocalContext.current
    LaunchedEffect(showWeek, state.schedule?.date, state.isLoadingWeek, state.weekLoadError, state.weeklySchedules.isEmpty()) {
        if (showWeek && state.weeklySchedules.isEmpty() && !state.isLoadingWeek && !state.weekLoadError) {
            viewModel.loadWeeklySchedules()
        }
    }
    LaunchedEffect(showMonth, state.displayedMonth, state.isLoadingMonth, state.monthLoadError, state.monthlySchedules.isEmpty()) {
        val month = state.displayedMonth
        if (showMonth && month != null && state.monthlySchedules.isEmpty() && !state.isLoadingMonth && !state.monthLoadError) {
            viewModel.changeDisplayedMonth(month)
        }
    }
    DisposableEffect(viewModel) {
        viewModel.startCountdownUpdates(enablePrayerAlerts = true)
        onDispose { viewModel.stopCountdownUpdates() }
    }
    val alarmManager = context.getSystemService(AlarmManager::class.java)
    fun saveOrRequestNotificationPermission(location: PrayerLocation, settings: PrayerSettings) {
        if (settings.notificationsEnabled && Build.VERSION.SDK_INT >= 33 &&
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            pendingNotificationSave = location to settings
            previewAfterPermission = false
            showNotificationPurpose = true
        } else {
            viewModel.saveManualLocation(location, settings)
        }
    }
    val locationPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        if (result.values.any { it }) viewModel.useCurrentDeviceLocation() else viewModel.onLocationPermissionDenied()
    }
    val notificationPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        pendingNotificationSave?.let { (pendingLocation, pendingSettings) ->
            viewModel.saveManualLocation(
                pendingLocation,
                pendingSettings.copy(notificationsEnabled = granted, adhanEnabled = granted && pendingSettings.adhanEnabled),
            )
        }
        if (granted && previewAfterPermission) viewModel.previewAdhan()
        previewAfterPermission = false
        pendingNotificationSave = null
    }
    val exactAlarmPermission = rememberLauncherForActivityResult(StartActivityForResult()) {
        pendingExactAlarmSave?.let { (location, settings) ->
            pendingExactAlarmSave = null
            saveOrRequestNotificationPermission(location, settings)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.prayer_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                },
                actions = {
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.prayer_settings))
                    }
                },
            )
        },
    ) { padding ->
        val location = state.configuration.location
        val schedule = state.schedule
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        location?.let { listOf(it.city, it.region, it.country).filter(String::isNotBlank).joinToString("، ") }
                            ?: stringResource(R.string.prayer_location_not_set),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                    )
                    location?.mosqueName?.let { mosque ->
                        Text(stringResource(R.string.prayer_main_mosque, mosque), style = MaterialTheme.typography.labelMedium)
                        Text(stringResource(R.string.prayer_osm_attribution), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    val zone = location?.let { runCatching { ZoneId.of(it.timeZoneId) }.getOrNull() } ?: ZoneId.systemDefault()
                    val date = state.now.atZone(zone).toLocalDate()
                    Text(
                        DateTimeFormatter.ofPattern("EEEE، d MMMM yyyy", Locale("ar")).format(date),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ar"))
                            .withChronology(HijrahChronology.INSTANCE).format(date),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (state.isLocating) {
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                        Text(stringResource(R.string.prayer_location_loading))
                    }
                }
            }
            if (state.locationUnavailable) {
                item { Text(stringResource(R.string.prayer_location_unavailable), color = MaterialTheme.colorScheme.error) }
            }
            if (state.calculationFailed) {
                item { Text(stringResource(R.string.prayer_calculation_error), color = MaterialTheme.colorScheme.error) }
            }
            if (location != null && schedule != null) {
                if (state.configuration.settings.adhanEnabled && Build.VERSION.SDK_INT >= 31 && !alarmManager.canScheduleExactAlarms()) {
                    item {
                        Text(
                            stringResource(R.string.prayer_exact_alarm_unavailable),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        TextButton(onClick = { showWeek = false; showMonth = false }) {
                            Text(stringResource(R.string.prayer_view_today), color = if (!showWeek && !showMonth) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        TextButton(onClick = { showWeek = true; showMonth = false }) {
                            Text(stringResource(R.string.prayer_view_week), color = if (showWeek) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        TextButton(onClick = { showWeek = false; showMonth = true }) {
                            Text(stringResource(R.string.prayer_view_month), color = if (showMonth) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                if (showWeek) {
                    if (state.isLoadingWeek) {
                        item {
                            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    } else if (state.weekLoadError) {
                        item {
                            TextButton(onClick = viewModel::loadWeeklySchedules, modifier = Modifier.fillMaxWidth()) {
                                Text(stringResource(R.string.prayer_week_unavailable))
                            }
                        }
                    } else items(state.weeklySchedules, key = { "week-${it.date}" }) { day ->
                        val zone = runCatching { ZoneId.of(location.timeZoneId) }.getOrDefault(ZoneId.systemDefault())
                        val fajr = day.times.first { it.name == PrayerName.FAJR }
                        val maghrib = day.times.first { it.name == PrayerName.MAGHRIB }
                        Row(
                            Modifier.fillMaxWidth().heightIn(min = 64.dp).padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                DateTimeFormatter.ofPattern("EEEE، d MMM", Locale("ar")).format(day.date),
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.titleSmall,
                            )
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${stringResource(R.string.prayer_fajr)}  ${DateTimeFormatter.ofPattern("HH:mm").withZone(zone).format(fajr.instant)}")
                                Text("${stringResource(R.string.prayer_maghrib)}  ${DateTimeFormatter.ofPattern("HH:mm").withZone(zone).format(maghrib.instant)}")
                            }
                        }
                        HorizontalDivider()
                    }
                }
                if (showMonth) {
                    val zone = runCatching { ZoneId.of(location.timeZoneId) }.getOrDefault(ZoneId.systemDefault())
                    val month = state.displayedMonth ?: YearMonth.from(schedule.date)
                    val currentMonth = YearMonth.from(state.now.atZone(zone).toLocalDate())
                    item {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            IconButton(onClick = { viewModel.changeDisplayedMonth(month.minusMonths(1)) }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.prayer_previous_month))
                            }
                            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    DateTimeFormatter.ofPattern("MMMM yyyy", Locale("ar")).format(month.atDay(1)),
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                Text(
                                    DateTimeFormatter.ofPattern("MMMM yyyy", Locale("ar"))
                                        .withChronology(HijrahChronology.INSTANCE).format(month.atDay(1)),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            IconButton(onClick = { viewModel.changeDisplayedMonth(month.plusMonths(1)) }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = stringResource(R.string.prayer_next_month))
                            }
                        }
                        if (month != currentMonth) {
                            TextButton(
                                onClick = { viewModel.changeDisplayedMonth(currentMonth) },
                                modifier = Modifier.fillMaxWidth(),
                            ) { Text(stringResource(R.string.prayer_current_month)) }
                        }
                    }
                    item { IslamicCalendarEvents(month) }
                    if (state.isLoadingMonth) {
                        item {
                            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    } else if (state.monthLoadError) {
                        item {
                            TextButton(
                                onClick = { viewModel.changeDisplayedMonth(month) },
                                modifier = Modifier.fillMaxWidth(),
                            ) { Text(stringResource(R.string.prayer_month_unavailable)) }
                        }
                    } else {
                        items(state.monthlySchedules, key = { "month-${it.date}" }) { day ->
                            val hijriDate = HijrahChronology.INSTANCE.date(day.date)
                            Column(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 10.dp)) {
                                Text(
                                    DateTimeFormatter.ofPattern("EEEE، d MMMM yyyy", Locale("ar")).format(day.date),
                                    style = MaterialTheme.typography.titleSmall,
                                )
                                Text(
                                    DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ar"))
                                        .withChronology(HijrahChronology.INSTANCE).format(hijriDate),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                day.times.chunked(3).forEach { row ->
                                    Row(
                                        Modifier.fillMaxWidth().padding(top = 8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    ) {
                                        row.forEach { prayerTime ->
                                            Column(
                                                Modifier.weight(1f),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                            ) {
                                                Text(prayerName(prayerTime.name), style = MaterialTheme.typography.labelSmall)
                                                Text(
                                                    DateTimeFormatter.ofPattern("HH:mm").withZone(zone).format(prayerTime.instant),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            HorizontalDivider()
                        }
                        if (state.monthlySchedules.isEmpty()) {
                            item { Text(stringResource(R.string.prayer_month_unavailable), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) }
                        }
                    }
                }
                if (!showWeek && !showMonth) {
                item {
                    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(stringResource(R.string.prayer_next), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        state.nextPrayer?.let { next ->
                            Text(prayerName(next.prayer.name), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                            Text(
                                stringResource(R.string.prayer_remaining, formatCountdown(next.remainingSeconds)),
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                        state.currentPrayer?.let {
                            Text(stringResource(R.string.prayer_current, prayerName(it)), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                items(schedule.times, key = { it.name.name }) { time ->
                    val isCurrent = time.name == state.currentPrayer
                                val status = when {
                        isCurrent -> R.string.prayer_status_current
                        time.instant.isBefore(state.now) -> R.string.prayer_status_past
                        else -> R.string.prayer_status_upcoming
                    }
                    val zone = runCatching { ZoneId.of(location.timeZoneId) }.getOrDefault(ZoneId.systemDefault())
                    Row(
                        Modifier.fillMaxWidth().heightIn(min = 58.dp).padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(prayerName(time.name), style = MaterialTheme.typography.titleMedium)
                            Text(stringResource(status), style = MaterialTheme.typography.labelSmall, color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(
                            DateTimeFormatter.ofPattern("HH:mm").withZone(zone).format(time.instant),
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 24.sp,
                            color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    HorizontalDivider()
                }
                item {
                    Text(
                        stringResource(methodName(state.configuration.settings.calculationMethod)),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                }
            } else if (location == null) {
                item {
                    Column(Modifier.fillMaxWidth().padding(vertical = 28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(stringResource(R.string.prayer_location_prompt), textAlign = TextAlign.Center)
                        Button(onClick = { showSettings = true }, modifier = Modifier.padding(top = 16.dp).heightIn(min = 48.dp)) {
                            Text(stringResource(R.string.prayer_set_location))
                        }
                    }
                }
            }
            item {
                OutlinedButton(onClick = { showSettings = true }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                    Text(stringResource(R.string.prayer_settings))
                }
            }
        }
    }

    if (showSettings) {
        PrayerSettingsSheet(
            location = state.configuration.location,
            settings = state.configuration.settings,
            citySuggestions = state.citySuggestions,
            isSearchingCities = state.isSearchingCities,
            isSearchingMainMosque = state.isSearchingMainMosque,
            mainMosqueSuggestion = state.mainMosqueSuggestion,
            onDismiss = { showSettings = false },
            onSave = { location, settings ->
                showSettings = false
                if (settings.adhanEnabled && Build.VERSION.SDK_INT >= 31 && !alarmManager.canScheduleExactAlarms()) {
                    pendingExactAlarmSave = location to settings
                    showExactAlarmPurpose = true
                } else {
                    saveOrRequestNotificationPermission(location, settings)
                }
            },
            onRequestDeviceLocation = { showLocationPurpose = true },
            onSearchCities = viewModel::searchCities,
            onFindMainMosque = viewModel::searchMainMosque,
            onPreviewAdhan = {
                if (Build.VERSION.SDK_INT >= 33 &&
                    context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                ) {
                    previewAfterPermission = true
                    showNotificationPurpose = true
                } else {
                    viewModel.previewAdhan()
                }
            },
        )
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
            dismissButton = {
                TextButton(onClick = { showLocationPurpose = false }) { Text(stringResource(R.string.action_close)) }
            },
        )
    }

    if (showNotificationPurpose) {
        AlertDialog(
            onDismissRequest = {
                showNotificationPurpose = false
                pendingNotificationSave?.let { (pendingLocation, pendingSettings) ->
                    viewModel.saveManualLocation(pendingLocation, pendingSettings.copy(notificationsEnabled = false, adhanEnabled = false))
                }
                pendingNotificationSave = null
                previewAfterPermission = false
            },
            title = { Text(stringResource(R.string.prayer_notification_permission_title)) },
            text = { Text(stringResource(R.string.prayer_notification_permission_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showNotificationPurpose = false
                    notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                }) { Text(stringResource(R.string.action_continue)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showNotificationPurpose = false
                    pendingNotificationSave?.let { (pendingLocation, pendingSettings) ->
                        viewModel.saveManualLocation(pendingLocation, pendingSettings.copy(notificationsEnabled = false, adhanEnabled = false))
                    }
                    pendingNotificationSave = null
                    previewAfterPermission = false
                }) { Text(stringResource(R.string.action_close)) }
            },
        )
    }

    if (showExactAlarmPurpose) {
        AlertDialog(
            onDismissRequest = {
                showExactAlarmPurpose = false
                pendingExactAlarmSave?.let { (location, settings) -> saveOrRequestNotificationPermission(location, settings) }
                pendingExactAlarmSave = null
            },
            title = { Text(stringResource(R.string.prayer_exact_alarm_title)) },
            text = { Text(stringResource(R.string.prayer_exact_alarm_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showExactAlarmPurpose = false
                    exactAlarmPermission.launch(
                        Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:${context.packageName}")),
                    )
                }) { Text(stringResource(R.string.action_continue)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showExactAlarmPurpose = false
                    pendingExactAlarmSave?.let { (location, settings) ->
                        saveOrRequestNotificationPermission(location, settings.copy(adhanEnabled = false))
                    }
                    pendingExactAlarmSave = null
                }) { Text(stringResource(R.string.action_close)) }
            },
        )
    }
}

@Composable
private fun prayerName(name: PrayerName): String = stringResource(
    when (name) {
        PrayerName.FAJR -> R.string.prayer_fajr
        PrayerName.SUNRISE -> R.string.prayer_sunrise
        PrayerName.DHUHR -> R.string.prayer_dhuhr
        PrayerName.ASR -> R.string.prayer_asr
        PrayerName.MAGHRIB -> R.string.prayer_maghrib
        PrayerName.ISHA -> R.string.prayer_isha
    },
)

@Composable
private fun methodName(method: com.quoren.app.domain.model.PrayerCalculationMethod): Int = when (method) {
    com.quoren.app.domain.model.PrayerCalculationMethod.MUSLIM_WORLD_LEAGUE -> R.string.prayer_method_mwl
    com.quoren.app.domain.model.PrayerCalculationMethod.EGYPTIAN -> R.string.prayer_method_egyptian
    com.quoren.app.domain.model.PrayerCalculationMethod.UMM_AL_QURA -> R.string.prayer_method_umm_al_qura
    com.quoren.app.domain.model.PrayerCalculationMethod.ISNA -> R.string.prayer_method_isna
    com.quoren.app.domain.model.PrayerCalculationMethod.GULF_REGION -> R.string.prayer_method_gulf
    com.quoren.app.domain.model.PrayerCalculationMethod.FRANCE_MOSQUE -> R.string.prayer_method_france
}

private fun formatCountdown(seconds: Long): String =
    String.format(Locale.ROOT, "%02d:%02d:%02d", seconds / 3600, seconds % 3600 / 60, seconds % 60)

@Composable
private fun IslamicCalendarEvents(month: YearMonth) {
    val events = remember(month) { IslamicCalendarEventCalculator().eventsIn(month) }
    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(stringResource(R.string.prayer_upcoming_events), style = MaterialTheme.typography.titleSmall)
        Text(stringResource(R.string.prayer_hijri_date_estimate), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (events.isEmpty()) {
            Text(stringResource(R.string.prayer_no_hijri_events), style = MaterialTheme.typography.bodySmall)
        } else {
            events.forEach { event ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(event.labelResource()), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.End) {
                        Text(DateTimeFormatter.ofPattern("d MMM yyyy", Locale("ar")).format(event.date), style = MaterialTheme.typography.bodySmall)
                        Text(
                            DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ar")).withChronology(HijrahChronology.INSTANCE).format(event.date),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

private fun IslamicCalendarEvent.labelResource(): Int = when (type) {
    IslamicEventType.HIJRI_NEW_YEAR -> R.string.prayer_hijri_new_year
    IslamicEventType.ASHURA -> R.string.prayer_ashura
    IslamicEventType.MAWLID -> R.string.prayer_mawlid
    IslamicEventType.ISRA_MIRAJ -> R.string.prayer_isra_miraj
    IslamicEventType.MID_SHAABAN -> R.string.prayer_mid_shaaban
    IslamicEventType.RAMADAN_START -> R.string.prayer_ramadan_start
    IslamicEventType.EID_FITR -> R.string.prayer_eid_fitr
    IslamicEventType.ARAFAH -> R.string.prayer_arafah
    IslamicEventType.EID_ADHA -> R.string.prayer_eid_adha
}

