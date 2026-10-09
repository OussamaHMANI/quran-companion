package com.quoren.app.feature.reader

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.quoren.app.R
import com.quoren.app.domain.memorization.OfflineAsrModelState
import com.quoren.app.domain.memorization.OfflineModelFailure
import com.quoren.app.domain.memorization.RecitationMatch
import com.quoren.app.domain.memorization.RecitationRecognitionState

@Composable
fun ReaderVoiceControls(
    state: ReaderVoicePracticeState,
    onToggle: () -> Unit,
    onRequestMicrophone: () -> Unit,
    onDownloadModel: () -> Unit,
    onRequestOnline: () -> Unit,
    onFinish: () -> Unit,
    onCancel: () -> Unit,
    onRevealPart: () -> Unit,
) {
    var showOnlineConsent by remember { mutableStateOf(false) }
    Surface(tonalElevation = 2.dp, color = MaterialTheme.colorScheme.surfaceVariant) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            if (state.totalWords > 0) {
                Text(
                    stringResource(R.string.reader_voice_word_progress, state.matchedWords, state.totalWords),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            state.feedback?.let { feedback ->
                val message = when (feedback) {
                    RecitationMatch.CORRECT -> R.string.hifdh_recitation_correct
                    RecitationMatch.INCORRECT -> R.string.hifdh_recitation_incorrect
                    RecitationMatch.UNCERTAIN -> R.string.hifdh_recitation_uncertain
                }
                Text(
                    stringResource(message),
                    color = if (feedback == RecitationMatch.INCORRECT) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
            val recognitionMessage = when (state.recognition) {
                RecitationRecognitionState.PermissionRequired -> R.string.permission_microphone_required
                RecitationRecognitionState.OnlineServiceUnavailable -> R.string.hifdh_online_service_unavailable
                RecitationRecognitionState.NetworkUnavailable -> R.string.hifdh_online_network_unavailable
                RecitationRecognitionState.Error -> R.string.err_unknown
                else -> null
            }
            recognitionMessage?.let { Text(stringResource(it), color = MaterialTheme.colorScheme.error) }
            if (state.retryingRecognition) {
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    LinearProgressIndicator(modifier = Modifier.weight(1f))
                    Text(stringResource(R.string.hifdh_recitation_retrying), modifier = Modifier.padding(start = 8.dp))
                }
            }
            if (state.complete) {
                Text(stringResource(R.string.reader_voice_complete), style = MaterialTheme.typography.titleSmall)
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    when (state.model) {
                        OfflineAsrModelState.Missing -> OutlinedButton(
                            onClick = onDownloadModel,
                            modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                        ) { Text(stringResource(R.string.hifdh_download_offline_model, 318)) }
                        is OfflineAsrModelState.Downloading -> {
                            val download = state.model as OfflineAsrModelState.Downloading
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                                if (download.progress == null) {
                                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                                    Text(stringResource(R.string.hifdh_model_download_starting), style = MaterialTheme.typography.labelSmall)
                                }
                                else {
                                    LinearProgressIndicator(progress = { download.progress / 100f }, modifier = Modifier.fillMaxWidth())
                                    Text(stringResource(R.string.hifdh_model_download_progress, download.progress), style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                        is OfflineAsrModelState.Failed -> {
                            val failure = state.model as OfflineAsrModelState.Failed
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                                Text(stringResource(failure.reason.messageResource()), color = MaterialTheme.colorScheme.error)
                                OutlinedButton(onClick = onDownloadModel, modifier = Modifier.heightIn(min = 48.dp)) {
                                    Text(stringResource(R.string.download_retry))
                                }
                            }
                        }
                        OfflineAsrModelState.Ready -> Unit
                    }
                    Button(
                        onClick = onRequestMicrophone,
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                    ) {
                        Text(
                            stringResource(
                                when {
                                    state.startingAfterModelDownload -> R.string.hifdh_starting_after_model_download
                                    state.model == OfflineAsrModelState.Ready -> R.string.hifdh_start_recognition
                                    state.model is OfflineAsrModelState.Downloading -> R.string.hifdh_start_when_model_ready
                                    else -> R.string.hifdh_start_after_download
                                },
                            ),
                        )
                    }
                }
                if (state.model != OfflineAsrModelState.Ready) {
                    OutlinedButton(onClick = { showOnlineConsent = true }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                        Text(stringResource(R.string.hifdh_online_recognition))
                    }
                }
                if (state.revealedPart < 4) {
                    TextButton(onClick = onRevealPart, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                        Text(stringResource(R.string.hifdh_reveal_next_part))
                    }
                }
                if (state.retryingRecognition || state.recognition == RecitationRecognitionState.Listening || state.recognition is RecitationRecognitionState.PartialTranscript) {
                    TextButton(onClick = if (state.retryingRecognition) onCancel else onFinish, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                        Text(stringResource(R.string.hifdh_stop_recitation))
                    }
                }
            }
            TextButton(onClick = onToggle, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                Text(stringResource(R.string.action_close))
            }
        }
    }

    if (showOnlineConsent) {
        AlertDialog(
            onDismissRequest = { showOnlineConsent = false },
            title = { Text(stringResource(R.string.hifdh_online_consent_title)) },
            text = { Text(stringResource(R.string.hifdh_online_consent_message)) },
            confirmButton = {
                TextButton(
                    onClick = { showOnlineConsent = false; onRequestOnline() },
                    modifier = Modifier.heightIn(min = 48.dp),
                ) { Text(stringResource(R.string.hifdh_online_consent_accept)) }
            },
            dismissButton = {
                TextButton(onClick = { showOnlineConsent = false }, modifier = Modifier.heightIn(min = 48.dp)) {
                    Text(stringResource(R.string.action_close))
                }
            },
        )
    }
}

private fun OfflineModelFailure.messageResource(): Int = when (this) {
    OfflineModelFailure.NETWORK -> R.string.hifdh_model_download_network_error
    OfflineModelFailure.STORAGE -> R.string.hifdh_model_download_storage_error
    OfflineModelFailure.INVALID_MODEL -> R.string.hifdh_model_download_invalid_error
    OfflineModelFailure.UNKNOWN -> R.string.hifdh_model_download_unknown_error
}
