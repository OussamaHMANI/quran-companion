package com.quoren.app.feature.audio

import android.text.format.Formatter
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quoren.app.R
import com.quoren.app.domain.model.AudioSettings
import com.quoren.app.domain.model.RepeatCount
import com.quoren.app.domain.model.StreamingQuality

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioSettingsScreen(onBack: () -> Unit, viewModel: AudioSettingsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.audio_settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize(), contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp)) {
            item { SectionTitle(R.string.default_reciter) }
            items(state.reciters, key = { it.id }) { reciter ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .clickable(role = Role.RadioButton) { viewModel.onReciterSelected(reciter.id) }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(selected = reciter.id == state.defaultReciterId, onClick = null)
                    Column(Modifier.padding(start = 16.dp)) {
                        Text(reciter.arabicName, style = MaterialTheme.typography.bodyLarge)
                        Text(reciter.englishName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            item { HorizontalDivider(Modifier.padding(vertical = 8.dp)) }
            item { SectionTitle(R.string.repeat_ayah) }
            item { RepeatChips(state.settings.ayahRepeat) { c -> viewModel.update { it.copy(ayahRepeat = c) } } }
            item { SectionTitle(R.string.repeat_range) }
            item { RepeatChips(state.settings.rangeRepeat) { c -> viewModel.update { it.copy(rangeRepeat = c) } } }
            item { SectionTitle(R.string.repeat_delay) }
            item { DelayChips(state.settings.repeatDelaySeconds) { s -> viewModel.update { it.copy(repeatDelaySeconds = s) } } }
            item { SectionTitle(R.string.streaming_quality) }
            item {
                ChipRow {
                    StreamingQuality.entries.forEach { q ->
                        FilterChip(
                            selected = state.settings.streamingQuality == q,
                            onClick = { viewModel.update { it.copy(streamingQuality = q) } },
                            label = { Text(stringResource(if (q == StreamingQuality.HIGH) R.string.quality_high else R.string.quality_low)) },
                        )
                    }
                }
            }
            item {
                Row(
                    Modifier.fillMaxWidth().heightIn(min = 56.dp).padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(stringResource(R.string.auto_download), Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                    Switch(
                        checked = state.settings.autoDownload,
                        onCheckedChange = { v -> viewModel.update { it.copy(autoDownload = v) } },
                    )
                }
            }
            item { SectionTitle(R.string.cache_limit) }
            item {
                ChipRow {
                    AudioSettings.CACHE_LIMIT_OPTIONS_MB.forEach { mb ->
                        FilterChip(
                            selected = state.settings.cacheLimitMb == mb,
                            onClick = { viewModel.update { it.copy(cacheLimitMb = mb) } },
                            label = { Text(stringResource(R.string.megabytes, mb)) },
                        )
                    }
                }
            }
            item {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        stringResource(R.string.cache_size, Formatter.formatFileSize(context, state.cacheSizeBytes)),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Button(onClick = viewModel::onClearCache, modifier = Modifier.heightIn(min = 48.dp)) {
                        Text(stringResource(R.string.clear_cache))
                    }
                }
            }
        }
    }
}

@Composable
internal fun SectionTitle(titleRes: Int) {
    Text(
        stringResource(titleRes),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp).semantics { },
    )
}

@Composable
internal fun ChipRow(content: @Composable () -> Unit) {
    Row(
        Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) { content() }
}

@Composable
internal fun RepeatChips(selected: RepeatCount, onSelect: (RepeatCount) -> Unit) {
    ChipRow {
        RepeatCount.entries.forEach { count ->
            FilterChip(
                selected = selected == count,
                onClick = { onSelect(count) },
                label = {
                    Text(
                        if (count.times == null) stringResource(R.string.repeat_unlimited) else stringResource(R.string.repeat_times, count.times),
                    )
                },
            )
        }
    }
}

@Composable
internal fun DelayChips(selected: Int, onSelect: (Int) -> Unit) {
    ChipRow {
        AudioSettings.DELAY_OPTIONS_SECONDS.forEach { seconds ->
            FilterChip(
                selected = selected == seconds,
                onClick = { onSelect(seconds) },
                label = { Text(stringResource(R.string.delay_seconds, seconds)) },
            )
        }
    }
}
