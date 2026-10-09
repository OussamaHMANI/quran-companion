package com.quoren.app.feature.quran

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quoren.app.R
import com.quoren.app.core.designsystem.QuranFontFamily
import com.quoren.app.domain.model.PrayerName
import com.quoren.app.feature.prayer.PrayerScreenState
import com.quoren.app.feature.backup.LocalBackupViewModel
import java.time.ZoneId
import java.time.chrono.HijrahChronology
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeDashboard(
    state: QuranIndexUiState,
    prayerState: PrayerScreenState,
    onOpenReader: (page: Int, ayahId: Int) -> Unit,
    onOpenSearch: () -> Unit,
    onOpenDownloads: () -> Unit,
    onOpenAudioSettings: () -> Unit,
    onOpenMemorization: () -> Unit,
    onOpenPrayer: () -> Unit,
    onOpenQibla: () -> Unit,
    onOpenMosques: () -> Unit,
    backupViewModel: LocalBackupViewModel = hiltViewModel(),
) {
    val backupState by backupViewModel.state.collectAsStateWithLifecycle()
    var pendingBackupImport by remember { mutableStateOf<Uri?>(null) }
    val exportBackup = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        uri?.let(backupViewModel::exportTo)
    }
    val importBackup = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        pendingBackupImport = uri
    }
    val location = prayerState.configuration.location
    val zone = location?.let { runCatching { ZoneId.of(it.timeZoneId) }.getOrNull() } ?: ZoneId.systemDefault()
    val date = prayerState.now.atZone(zone).toLocalDate()
    LazyColumn(
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Row(
                Modifier.fillMaxWidth().padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.size(56.dp),
                ) {
                    Icon(
                        painterResource(R.drawable.ic_launcher_foreground),
                        contentDescription = null,
                        tint = androidx.compose.ui.graphics.Color.Unspecified,
                        modifier = Modifier.padding(5.dp),
                    )
                }
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.home_greeting), style = MaterialTheme.typography.headlineSmall)
                    Text(
                        DateTimeFormatter.ofPattern("EEEE، d MMMM yyyy", Locale("ar")).format(date),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ar"))
                            .withChronology(HijrahChronology.INSTANCE).format(date),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
            }
        }
        item { NextPrayerSection(prayerState, onOpenPrayer) }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.home_quick_access), style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuickAccessButton(R.string.tab_memorization, onOpenMemorization, Modifier.weight(1f)) {
                        Icon(painterResource(R.drawable.ic_hifdh), contentDescription = null)
                    }
                    QuickAccessButton(R.string.action_search, onOpenSearch, Modifier.weight(1f)) {
                        Icon(Icons.Filled.Search, contentDescription = null)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuickAccessButton(R.string.action_reciters, onOpenAudioSettings, Modifier.weight(1f)) {
                        Icon(Icons.Filled.Person, contentDescription = null)
                    }
                    QuickAccessButton(R.string.action_downloads, onOpenDownloads, Modifier.weight(1f)) {
                        Icon(painterResource(R.drawable.ic_download), contentDescription = null)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuickAccessButton(R.string.qibla_title, onOpenQibla, Modifier.weight(1f)) {
                        Icon(Icons.Filled.LocationOn, contentDescription = null)
                    }
                    QuickAccessButton(R.string.mosque_title, onOpenMosques, Modifier.weight(1f)) {
                        Icon(Icons.Filled.LocationOn, contentDescription = null)
                    }
                }
            }
        }
        state.continueReading?.let { reading ->
            item {
                Text(stringResource(R.string.home_reading), style = MaterialTheme.typography.titleMedium)
                Card(
                    onClick = { onOpenReader(reading.progress.page, reading.progress.ayahId) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                reading.surahName.ifBlank { stringResource(R.string.tab_surahs) },
                                fontFamily = QuranFontFamily,
                                style = MaterialTheme.typography.titleLarge,
                            )
                            Text(stringResource(R.string.home_continue_page, reading.progress.page), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Button(onClick = { onOpenReader(reading.progress.page, reading.progress.ayahId) }) {
                            Text(stringResource(R.string.home_continue))
                        }
                    }
                }
            }
        }
        item {
            Text(stringResource(R.string.home_browse), style = MaterialTheme.typography.titleMedium)
            Row(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = { onOpenReader(state.surahs.firstOrNull()?.firstPage ?: 1, 0) },
                    modifier = Modifier.weight(1f).heightIn(min = 52.dp),
                ) { Text(stringResource(R.string.home_start_reading)) }
                OutlinedButton(onClick = onOpenPrayer, modifier = Modifier.weight(1f).heightIn(min = 52.dp)) {
                    Text(stringResource(R.string.prayer_title))
                }
            }
        }
        item {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(stringResource(R.string.backup_title), style = MaterialTheme.typography.titleMedium)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        enabled = !backupState.busy,
                        onClick = { exportBackup.launch("quoren-user-backup.json") },
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                    ) { Text(stringResource(R.string.backup_export)) }
                    OutlinedButton(
                        enabled = !backupState.busy,
                        onClick = { importBackup.launch(arrayOf("application/json", "text/plain")) },
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                    ) { Text(stringResource(R.string.backup_import)) }
                }
                if (backupState.busy) CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                backupState.messageResource?.let { message ->
                    TextButton(onClick = backupViewModel::clearMessage, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(message), textAlign = TextAlign.Center)
                    }
                }
            }
        }
        item {
            Text(
                stringResource(R.string.app_developed_by),
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
    pendingBackupImport?.let { uri ->
        AlertDialog(
            onDismissRequest = { pendingBackupImport = null },
            title = { Text(stringResource(R.string.backup_import_confirm_title)) },
            text = { Text(stringResource(R.string.backup_import_confirm_message)) },
            confirmButton = {
                Button(onClick = {
                    backupViewModel.importFrom(uri)
                    pendingBackupImport = null
                }) { Text(stringResource(R.string.backup_import)) }
            },
            dismissButton = { TextButton(onClick = { pendingBackupImport = null }) { Text(stringResource(R.string.action_close)) } },
        )
    }
}

@Composable
private fun QuickAccessButton(
    labelRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 54.dp),
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon()
            Text(stringResource(labelRes), maxLines = 1)
        }
    }
}

@Composable
private fun NextPrayerSection(state: PrayerScreenState, onOpenPrayer: () -> Unit) {
    Surface(
        onClick = onOpenPrayer,
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(horizontal = 20.dp, vertical = 18.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(stringResource(R.string.prayer_next), style = MaterialTheme.typography.labelLarge)
            if (state.nextPrayer != null) {
                Text(
                    prayerName(state.nextPrayer.prayer.name),
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = QuranFontFamily,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    stringResource(R.string.prayer_remaining, formatCountdown(state.nextPrayer.remainingSeconds)),
                    style = MaterialTheme.typography.titleMedium,
                )
                state.configuration.location?.let {
                    Text(it.city, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                Text(stringResource(R.string.home_prayer_location_missing), style = MaterialTheme.typography.titleMedium)
                Text(stringResource(R.string.home_set_prayer_location), style = MaterialTheme.typography.bodyMedium)
            }
        }
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

private fun formatCountdown(seconds: Long): String = String.format(Locale.ROOT, "%02d:%02d:%02d", seconds / 3600, seconds % 3600 / 60, seconds % 60)
