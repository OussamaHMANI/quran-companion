package com.quoren.app.feature.audio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quoren.app.R
import com.quoren.app.core.designsystem.QuranFontFamily
import com.quoren.app.domain.model.PlaybackStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullPlayerScreen(onBack: () -> Unit, viewModel: PlayerViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val position by viewModel.position.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.action_player)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                },
            )
        },
    ) { padding ->
        if (!state.isActive) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.action_play_audio), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            return@Scaffold
        }
        Column(
            Modifier.padding(padding).fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        ) {
            val photoDescription = stringResource(R.string.reciter_photo)
            Box(
                Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .semantics { contentDescription = photoDescription },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(88.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Text(state.reciterName.orEmpty(), style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
            Text(
                stringResource(R.string.surah_header, state.surahName.orEmpty()),
                style = MaterialTheme.typography.headlineSmall,
                fontFamily = QuranFontFamily,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                stringResource(R.string.player_ayah_progress, state.ayahIndex + 1, state.ayahCount),
                style = MaterialTheme.typography.bodyLarge,
            )
            if (state.ayahRepeat.times != 1) {
                val total = state.ayahRepeat.times?.toString() ?: stringResource(R.string.repeat_unlimited)
                Text(
                    stringResource(R.string.player_repeat_pass, state.ayahPass, total),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            state.error?.let {
                Text(stringResource(it.messageRes()), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyLarge)
            }

            var dragging by remember { mutableStateOf<Float?>(null) }
            val fraction = position.progress
            Slider(
                value = dragging ?: fraction,
                onValueChange = { dragging = it },
                onValueChangeFinished = {
                    dragging?.let(viewModel::seekToProgress)
                    dragging = null
                },
                enabled = position.mediaItemCount > 0,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.player_range_ayah, state.rangeStart), style = MaterialTheme.typography.labelMedium)
                Text(stringResource(R.string.player_range_ayah, state.rangeEnd), style = MaterialTheme.typography.labelMedium)
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = viewModel::previous, modifier = Modifier.size(56.dp)) {
                    Icon(painterResource(R.drawable.ic_skip_previous), stringResource(R.string.action_previous), Modifier.size(32.dp))
                }
                IconButton(onClick = viewModel::togglePlayPause, modifier = Modifier.size(72.dp)) {
                    if (state.status == PlaybackStatus.BUFFERING) {
                        CircularProgressIndicator(Modifier.size(40.dp))
                    } else {
                        val playing = state.isPlaying
                        Icon(
                            painterResource(if (playing) R.drawable.ic_pause else R.drawable.ic_play),
                            stringResource(if (playing) R.string.action_pause else R.string.action_play),
                            Modifier.size(48.dp),
                        )
                    }
                }
                IconButton(onClick = viewModel::next, modifier = Modifier.size(56.dp)) {
                    Icon(painterResource(R.drawable.ic_skip_next), stringResource(R.string.action_next), Modifier.size(32.dp))
                }
                IconButton(onClick = {
                    viewModel.stop()
                    onBack()
                }, modifier = Modifier.size(56.dp)) {
                    Icon(painterResource(R.drawable.ic_stop), stringResource(R.string.action_stop), Modifier.size(28.dp))
                }
            }
        }
    }
}
