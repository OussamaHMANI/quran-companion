package com.quoren.app.feature.reader

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quoren.app.R
import com.quoren.app.feature.audio.AudioSettingsViewModel
import com.quoren.app.feature.audio.DelayChips
import com.quoren.app.feature.audio.RepeatChips

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaybackSheet(
    ayahCount: Int,
    followText: Boolean,
    onFollowTextChanged: (Boolean) -> Unit,
    onPlay: (start: Int, end: Int?, reciterId: String) -> Unit,
    onDismiss: () -> Unit,
    viewModel: AudioSettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var fromText by remember { mutableStateOf("1") }
    var toText by remember { mutableStateOf(ayahCount.toString()) }
    var selectedReciterId by rememberSaveable(state.defaultReciterId) { mutableStateOf(state.defaultReciterId) }
    var reciterMenuExpanded by remember { mutableStateOf(false) }
    val from = fromText.toIntOrNull()
    val to = toText.toIntOrNull()
    val rangeValid = from != null && to != null && from in 1..ayahCount && to in from..ayahCount

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .navigationBarsPadding()
                .padding(top = 8.dp),
        ) {
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(stringResource(R.string.playback_options), style = MaterialTheme.typography.titleLarge)
                Row(
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.follow_recitation_text),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Switch(checked = followText, onCheckedChange = onFollowTextChanged)
                }
                Box {
                    TextButton(onClick = { reciterMenuExpanded = true }, modifier = Modifier.heightIn(min = 48.dp)) {
                        Text(
                            stringResource(
                                R.string.reciter_label,
                                state.reciters.firstOrNull { it.id == selectedReciterId }?.arabicName.orEmpty(),
                            ),
                        )
                    }
                    DropdownMenu(expanded = reciterMenuExpanded, onDismissRequest = { reciterMenuExpanded = false }) {
                        state.reciters.forEach { reciter ->
                            DropdownMenuItem(
                                text = { Text(reciter.arabicName) },
                                onClick = {
                                    selectedReciterId = reciter.id
                                    viewModel.onReciterSelected(reciter.id)
                                    reciterMenuExpanded = false
                                },
                            )
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = fromText,
                        onValueChange = { fromText = it.filter(Char::isDigit).take(3) },
                        label = { Text(stringResource(R.string.from_ayah)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        isError = !rangeValid,
                        modifier = Modifier.weight(1f),
                    )
                    OutlinedTextField(
                        value = toText,
                        onValueChange = { toText = it.filter(Char::isDigit).take(3) },
                        label = { Text(stringResource(R.string.to_ayah)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        isError = !rangeValid,
                        modifier = Modifier.weight(1f),
                    )
                }
                Text(stringResource(R.string.repeat_ayah), style = MaterialTheme.typography.titleSmall)
                RepeatChips(state.settings.ayahRepeat) { c -> viewModel.update { it.copy(ayahRepeat = c) } }
                Text(stringResource(R.string.repeat_range), style = MaterialTheme.typography.titleSmall)
                RepeatChips(state.settings.rangeRepeat) { c -> viewModel.update { it.copy(rangeRepeat = c) } }
                Text(stringResource(R.string.repeat_delay), style = MaterialTheme.typography.titleSmall)
                DelayChips(state.settings.repeatDelaySeconds) { s -> viewModel.update { it.copy(repeatDelaySeconds = s) } }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 8.dp)) {
                Button(onClick = { onPlay(1, null, selectedReciterId) }, modifier = Modifier.weight(1f).heightIn(min = 48.dp)) {
                    Text(stringResource(R.string.play_surah))
                }
                OutlinedButton(
                    onClick = { onPlay(from!!, to, selectedReciterId) },
                    enabled = rangeValid,
                    modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                ) {
                    Text(stringResource(R.string.play_range))
                }
            }
        }
    }
}
