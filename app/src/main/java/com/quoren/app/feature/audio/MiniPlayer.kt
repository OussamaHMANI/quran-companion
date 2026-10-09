package com.quoren.app.feature.audio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quoren.app.R

@Composable
fun MiniPlayer(onOpenPlayer: () -> Unit, modifier: Modifier = Modifier, viewModel: PlayerViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    if (!state.isActive) return

    Surface(
        onClick = onOpenPlayer,
        tonalElevation = 3.dp,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            Modifier
                .navigationBarsPadding()
                .heightIn(min = 64.dp)
                .padding(start = 16.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Column(Modifier.weight(1f).semantics(mergeDescendants = true) {}) {
                Text(
                    text = stringResource(R.string.audio_item_title, state.surahName.orEmpty(), state.ayahNumber ?: 0),
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                val error = state.error
                Text(
                    text = if (error != null) stringResource(error.messageRes()) else state.reciterName.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (error != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            IconButton(onClick = viewModel::togglePlayPause, modifier = Modifier.heightIn(min = 48.dp)) {
                val playing = state.isPlaying
                Icon(
                    painter = painterResource(if (playing) R.drawable.ic_pause else R.drawable.ic_play),
                    contentDescription = stringResource(if (playing) R.string.action_pause else R.string.action_play),
                )
            }
            IconButton(onClick = viewModel::stop) {
                Icon(painterResource(R.drawable.ic_stop), contentDescription = stringResource(R.string.action_stop))
            }
        }
    }
}

