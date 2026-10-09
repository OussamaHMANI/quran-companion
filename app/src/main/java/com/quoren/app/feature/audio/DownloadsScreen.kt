package com.quoren.app.feature.audio

import android.text.format.Formatter
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quoren.app.R
import com.quoren.app.core.designsystem.QuranFontFamily
import com.quoren.app.domain.model.DownloadStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen(onBack: () -> Unit, viewModel: DownloadsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.downloads_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize()) {
            item {
                Text(
                    stringResource(R.string.storage_usage, Formatter.formatFileSize(context, state.totalBytes)),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp),
                )
            }
            if (state.items.isEmpty()) {
                item {
                    Text(
                        stringResource(R.string.no_downloads),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }
            }
            items(state.items, key = { it.download.reciterId + ":" + it.download.surahId }) { item ->
                DownloadRow(item, viewModel)
                HorizontalDivider()
            }
            item { SectionTitle(R.string.available_surahs) }
            items(state.availableSurahs, key = { "s" + it.id }) { surah ->
                Row(
                    Modifier.fillMaxWidth().heightIn(min = 56.dp).padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(surah.nameArabic, style = MaterialTheme.typography.titleMedium, fontFamily = QuranFontFamily)
                    IconButton(onClick = { viewModel.onDownload(surah.id) }) {
                        Icon(
                            painterResource(R.drawable.ic_download),
                            stringResource(R.string.download_action) + " " + surah.nameArabic,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DownloadRow(item: DownloadItemUi, viewModel: DownloadsViewModel) {
    val d = item.download
    val context = LocalContext.current
    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Column(Modifier.semantics(mergeDescendants = true) {}) {
            Text(item.surahName, style = MaterialTheme.typography.titleMedium, fontFamily = QuranFontFamily)
            Text(item.reciterName, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            val status = when (d.status) {
                DownloadStatus.QUEUED -> stringResource(R.string.status_queued)
                DownloadStatus.DOWNLOADING -> stringResource(R.string.status_downloading, d.downloadedAyahs, d.totalAyahs)
                DownloadStatus.PAUSED -> stringResource(R.string.status_paused)
                DownloadStatus.COMPLETED -> stringResource(R.string.status_completed) + " · " + Formatter.formatFileSize(context, d.fileSize)
                DownloadStatus.FAILED -> stringResource(R.string.status_failed) + (d.error?.let { " · " + stringResource(it.messageRes()) } ?: "")
            }
            Text(
                status,
                style = MaterialTheme.typography.bodyMedium,
                color = if (d.status == DownloadStatus.FAILED) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            )
        }
        if (d.status != DownloadStatus.COMPLETED) {
            LinearProgressIndicator(progress = { d.progress }, modifier = Modifier.fillMaxWidth())
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            when (d.status) {
                DownloadStatus.QUEUED, DownloadStatus.DOWNLOADING ->
                    TextButton(onClick = { viewModel.onPause(d) }, Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.download_pause)) }
                DownloadStatus.PAUSED ->
                    TextButton(onClick = { viewModel.onResume(d) }, Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.download_resume)) }
                DownloadStatus.FAILED ->
                    TextButton(onClick = { viewModel.onRetry(d) }, Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.download_retry)) }
                DownloadStatus.COMPLETED -> Unit
            }
            TextButton(onClick = { viewModel.onDelete(d) }, Modifier.heightIn(min = 48.dp)) {
                Icon(painterResource(R.drawable.ic_delete), contentDescription = null)
                Text(stringResource(R.string.download_delete), Modifier.padding(start = 4.dp))
            }
        }
    }
}
