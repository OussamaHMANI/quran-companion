package com.quoren.app.feature.memorization

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.provider.Settings
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quoren.app.R
import com.quoren.app.core.designsystem.QuranFontFamily
import com.quoren.app.domain.memorization.MemorizationStatus
import com.quoren.app.domain.memorization.OfflineAsrModelState
import com.quoren.app.domain.memorization.OfflineModelFailure
import com.quoren.app.domain.memorization.RecitationRecognitionState
import com.quoren.app.domain.model.DifficultyRating
import com.quoren.app.domain.model.MemorizationMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HifdhSessionScreen(
    onBack: () -> Unit,
    viewModel: HifdhSessionViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showOnlineConsent by remember { mutableStateOf(false) }
    var pendingOnlineRecognition by remember { mutableStateOf(false) }
    val session = state.session
    val ayah = state.ayah
    val context = LocalContext.current
    val hasMicrophonePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    val requestMicPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            if (pendingOnlineRecognition) viewModel.startOnlineRecognition() else viewModel.startVoiceRecognition()
        } else {
            viewModel.setRecognitionPermissionDenied()
        }
        pendingOnlineRecognition = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.hifdh_session_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                },
            )
        },
    ) { padding ->
        when {
            state.loading -> Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            state.error != null && session == null -> Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.load_error), color = MaterialTheme.colorScheme.error)
            }
            state.complete -> Column(
                Modifier.padding(padding).fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(stringResource(R.string.hifdh_session_complete), style = MaterialTheme.typography.headlineSmall)
                state.ayah?.let { completedAyah ->
                    Text(
                        completedAyah.textUthmani,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                        fontFamily = QuranFontFamily,
                        fontSize = 28.sp,
                        lineHeight = 48.sp,
                        textAlign = TextAlign.Center,
                    )
                }
                Button(onClick = onBack, modifier = Modifier.padding(top = 20.dp).heightIn(min = 48.dp)) {
                    Text(stringResource(R.string.hifdh_close_session))
                }
            }
            session != null && ayah != null -> SessionContent(
                modifier = Modifier.padding(padding),
                viewModel = viewModel,
                state = state,
                requestMicrophonePermission = { requestMicPermission.launch(Manifest.permission.RECORD_AUDIO) },
                hasMicrophonePermission = hasMicrophonePermission,
                requestOnlineConsent = { showOnlineConsent = true },
            )
        }
    }

    if (showOnlineConsent) {
        AlertDialog(
            onDismissRequest = { showOnlineConsent = false },
            title = { Text(stringResource(R.string.hifdh_online_consent_title)) },
            text = { Text(stringResource(R.string.hifdh_online_consent_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showOnlineConsent = false
                        if (hasMicrophonePermission) viewModel.startOnlineRecognition() else {
                            pendingOnlineRecognition = true
                            requestMicPermission.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    },
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

@Composable
private fun SessionContent(
    modifier: Modifier,
    viewModel: HifdhSessionViewModel,
    state: HifdhSessionState,
    requestMicrophonePermission: () -> Unit,
    hasMicrophonePermission: Boolean,
    requestOnlineConsent: () -> Unit,
) {
    val session = checkNotNull(state.session)
    val ayah = checkNotNull(state.ayah)
    if (session.mode == MemorizationMode.VOICE_RECALL) {
        VoiceRecitationPage(
            modifier = modifier,
            state = state,
            viewModel = viewModel,
            hasMicrophonePermission = hasMicrophonePermission,
            requestMicrophonePermission = requestMicrophonePermission,
            requestOnlineConsent = requestOnlineConsent,
        )
        return
    }
    val recallMode = session.mode == MemorizationMode.LISTEN_RECALL || session.mode == MemorizationMode.VOICE_RECALL
    val hidden = recallMode && !state.ayahVisible
    val visualText = when {
        session.mode == MemorizationMode.PROGRESSIVE_HIDING -> progressiveAyah(ayah.textUthmani, state.hideLevel)
        session.mode == MemorizationMode.VOICE_RECALL && hidden -> revealedAyahPart(ayah.textUthmani, state.revealedPart)
        hidden -> AnnotatedString("••••••••••••••••")
        else -> AnnotatedString(ayah.textUthmani)
    }

    Column(
        modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            stringResource(R.string.ayah_ref, state.surahName, ayah.ayahNumber),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            "${session.currentAyah - session.startAyah + 1} / ${session.endAyah - session.startAyah + 1}",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = visualText,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
                .semantics { if (hidden && visualText.text.startsWith("••")) contentDescription = "الآية مخفية" },
            fontFamily = QuranFontFamily,
            fontSize = 34.sp,
            lineHeight = 62.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
        )
        state.progress?.let { Text(stringResource(it.status.labelRes()), color = MaterialTheme.colorScheme.onSurfaceVariant) }
        state.error?.let { error ->
            val message = when (error) {
                "mismatch" -> R.string.hifdh_recitation_incorrect
                "uncertain" -> R.string.hifdh_recitation_uncertain
                else -> R.string.err_unknown
            }
            Text(stringResource(message), color = MaterialTheme.colorScheme.error)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = viewModel::previousAyah, modifier = Modifier.size(56.dp)) {
                Icon(painterResource(R.drawable.ic_skip_previous), contentDescription = stringResource(R.string.hifdh_previous_ayah))
            }
            IconButton(onClick = viewModel::playCurrentAyah, modifier = Modifier.size(64.dp)) {
                Icon(Icons.Filled.PlayArrow, contentDescription = stringResource(R.string.action_play), modifier = Modifier.size(40.dp))
            }
            IconButton(onClick = viewModel::nextAyah, modifier = Modifier.size(56.dp)) {
                Icon(painterResource(R.drawable.ic_skip_next), contentDescription = stringResource(R.string.hifdh_next_ayah))
            }
        }
        when (session.mode) {
            MemorizationMode.READ_LISTEN -> Text(stringResource(R.string.hifdh_mode_read_listen), color = MaterialTheme.colorScheme.onSurfaceVariant)
            MemorizationMode.LISTEN_RECALL -> {
                if (!state.ayahVisible) {
                    Button(onClick = viewModel::revealAyah, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) {
                        Text(stringResource(R.string.hifdh_reveal_ayah))
                    }
                }
                if (state.ayahVisible) RatingActions(viewModel)
            }
            MemorizationMode.VOICE_RECALL -> {
                Unit
            }
            MemorizationMode.PROGRESSIVE_HIDING -> {
                Text(stringResource(R.string.hifdh_hide_level, state.hideLevel), style = MaterialTheme.typography.titleSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = { viewModel.changeHideLevel(-1) }, modifier = Modifier.weight(1f).heightIn(min = 48.dp)) {
                        Text(stringResource(R.string.hifdh_less_hidden))
                    }
                    Button(onClick = { viewModel.changeHideLevel(1) }, modifier = Modifier.weight(1f).heightIn(min = 48.dp)) {
                        Text(stringResource(R.string.hifdh_more_hidden))
                    }
                }
            }
            MemorizationMode.GROUP -> Button(
                onClick = viewModel::playGroup,
                modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
            ) { Text(stringResource(R.string.hifdh_repeat_group)) }
        }
        if (session.mode != MemorizationMode.VOICE_RECALL) {
            TextButton(onClick = viewModel::nextAyah, modifier = Modifier.heightIn(min = 48.dp)) {
                Text(stringResource(R.string.hifdh_next_ayah))
            }
        }
    }
}

@Composable
private fun VoiceRecitationPage(
    modifier: Modifier,
    state: HifdhSessionState,
    viewModel: HifdhSessionViewModel,
    hasMicrophonePermission: Boolean,
    requestMicrophonePermission: () -> Unit,
    requestOnlineConsent: () -> Unit,
) {
    val context = LocalContext.current
    val session = checkNotNull(state.session)
    val currentAyah = checkNotNull(state.ayah)
    val partialText = when (val recognized = state.recognition) {
        is RecitationRecognitionState.PartialTranscript -> recognized.transcript
        is RecitationRecognitionState.Result -> recognized.transcript
        else -> state.lastAttemptTranscript.orEmpty()
    }
    val visibleAttemptText = if (state.mismatchWordIndex != null) {
        partialText.split(Regex("\\s+")).filter(String::isNotBlank).take(state.matchedWords).joinToString(" ")
    } else partialText
    val ayahNumbers = remember(session.startAyah, session.endAyah) {
        (session.startAyah..session.endAyah).toList()
    }
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()
    androidx.compose.runtime.LaunchedEffect(session.currentAyah, session.startAyah) {
        listState.animateScrollToItem((session.currentAyah - session.startAyah).coerceAtLeast(0))
    }
    Column(modifier.fillMaxSize().navigationBarsPadding().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            stringResource(R.string.ayah_ref, state.surahName, session.currentAyah),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            textAlign = TextAlign.Center,
        )
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            IconButton(onClick = viewModel::previousAyah, modifier = Modifier.size(56.dp)) {
                Icon(painterResource(R.drawable.ic_skip_previous), contentDescription = stringResource(R.string.hifdh_previous_ayah))
            }
            IconButton(onClick = viewModel::playCurrentAyah, modifier = Modifier.size(64.dp)) {
                Icon(Icons.Filled.PlayArrow, contentDescription = stringResource(R.string.action_play), modifier = Modifier.size(40.dp))
            }
            IconButton(onClick = viewModel::nextAyah, modifier = Modifier.size(56.dp)) {
                Icon(painterResource(R.drawable.ic_skip_next), contentDescription = stringResource(R.string.hifdh_next_ayah))
            }
        }
        LazyColumn(
            Modifier.fillMaxWidth().weight(1f),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(ayahNumbers, key = { it }) { ayahNumber ->
                val isCurrent = ayahNumber == session.currentAyah
                Column(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .heightIn(min = if (isCurrent) 88.dp else 72.dp)
                            .then(if (isCurrent) Modifier.semantics { contentDescription = "الآية الحالية" } else Modifier)
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Text(
                            ayahMarker(ayahNumber),
                            color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = QuranFontFamily,
                        )
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            if (isCurrent && visibleAttemptText.isNotBlank()) {
                                Text(
                                    buildVoiceTranscript(
                                        transcript = visibleAttemptText,
                                        matchedWords = state.matchedWords,
                                        mismatchWordIndex = state.mismatchWordIndex,
                                        correctColor = MaterialTheme.colorScheme.primary,
                                        mismatchColor = MaterialTheme.colorScheme.error,
                                        defaultColor = MaterialTheme.colorScheme.onBackground,
                                    ),
                                    fontFamily = QuranFontFamily,
                                    fontSize = 25.sp,
                                    lineHeight = 42.sp,
                                )
                            } else if (isCurrent && state.revealedPart > 0) {
                                Text(
                                    revealedAyahPart(currentAyah.textUthmani, state.revealedPart).text,
                                    fontFamily = QuranFontFamily,
                                    fontSize = 25.sp,
                                    lineHeight = 42.sp,
                                    color = MaterialTheme.colorScheme.onBackground,
                                )
                            } else if (state.recitedAyahTexts[ayahNumber] != null) {
                                Text(
                                    state.recitedAyahTexts.getValue(ayahNumber),
                                    fontFamily = QuranFontFamily,
                                    fontSize = 25.sp,
                                    lineHeight = 42.sp,
                                    color = MaterialTheme.colorScheme.onBackground,
                                )
                            } else {
                                Spacer(Modifier.fillMaxWidth().heightIn(min = 42.dp))
                            }
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }
            }
        }
        if (state.totalWords > 0) {
            Text(
                stringResource(R.string.reader_voice_word_progress, state.matchedWords, state.totalWords),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        state.error?.let { error ->
            val message = when (error) {
                "mismatch" -> R.string.hifdh_recitation_incorrect
                "uncertain" -> R.string.hifdh_recitation_uncertain
                else -> R.string.err_unknown
            }
            Text(stringResource(message), color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 4.dp))
        }
        if (state.revealedPart < 4) {
            OutlinedButton(
                onClick = viewModel::revealNextPart,
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
            ) {
                Text(stringResource(R.string.hifdh_reveal_next_part))
            }
        }
        when (state.offlineModel) {
            com.quoren.app.domain.memorization.OfflineAsrModelState.Missing -> Button(
                onClick = viewModel::downloadOfflineModel,
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
            ) { Text(stringResource(R.string.hifdh_download_offline_model, 318)) }
            is OfflineAsrModelState.Downloading -> {
                val downloading = state.offlineModel as OfflineAsrModelState.Downloading
                if (downloading.progress == null) {
                    androidx.compose.material3.LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Text(stringResource(R.string.hifdh_model_download_starting), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                else {
                    androidx.compose.material3.LinearProgressIndicator(progress = { downloading.progress / 100f }, modifier = Modifier.fillMaxWidth())
                    Text(stringResource(R.string.hifdh_model_download_progress, downloading.progress))
                }
            }
            is OfflineAsrModelState.Failed -> {
                val failure = state.offlineModel as OfflineAsrModelState.Failed
                Text(stringResource(failure.reason.messageResource()), color = MaterialTheme.colorScheme.error)
                Button(onClick = viewModel::downloadOfflineModel, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                    Text(stringResource(R.string.download_retry))
                }
            }
            com.quoren.app.domain.memorization.OfflineAsrModelState.Ready -> Unit
        }
        recognitionMessage(state.recognition)?.let { Text(stringResource(it), color = MaterialTheme.colorScheme.onSurfaceVariant) }
        if (state.recognition == RecitationRecognitionState.OnlineServiceUnavailable) {
            OutlinedButton(
                onClick = {
                    runCatching {
                        context.startActivity(Intent(Settings.ACTION_VOICE_INPUT_SETTINGS))
                    }
                },
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
            ) { Text(stringResource(R.string.hifdh_open_voice_settings)) }
        }
        Button(
            onClick = if (hasMicrophonePermission) ({ viewModel.startOfflineRecognitionWhenReady() }) else requestMicrophonePermission,
            modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
        ) {
            Text(
                stringResource(
                    when {
                        state.startingAfterModelDownload -> R.string.hifdh_starting_after_model_download
                        state.offlineModel == OfflineAsrModelState.Ready -> R.string.hifdh_start_recognition
                        state.offlineModel is OfflineAsrModelState.Downloading -> R.string.hifdh_start_when_model_ready
                        else -> R.string.hifdh_start_after_download
                    },
                ),
            )
        }
        if (state.retryingRecognition) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                Text(stringResource(R.string.hifdh_recitation_retrying), modifier = Modifier.padding(start = 10.dp))
            }
        }
        OutlinedButton(onClick = requestOnlineConsent, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
            Text(stringResource(R.string.hifdh_online_recognition))
        }
        if (state.retryingRecognition || state.recognition == RecitationRecognitionState.Listening || state.recognition is RecitationRecognitionState.PartialTranscript) {
            TextButton(
                onClick = if (state.retryingRecognition) viewModel::stopVoiceRecognition else viewModel::finishVoiceRecognition,
                modifier = Modifier.heightIn(min = 48.dp),
            ) {
                Text(stringResource(R.string.hifdh_stop_recitation))
            }
        }
    }
}

private fun buildVoiceTranscript(
    transcript: String,
    matchedWords: Int,
    mismatchWordIndex: Int?,
    correctColor: androidx.compose.ui.graphics.Color,
    mismatchColor: androidx.compose.ui.graphics.Color,
    defaultColor: androidx.compose.ui.graphics.Color,
): AnnotatedString = buildAnnotatedString {
    transcript.split(Regex("\\s+")).filter(String::isNotBlank).forEachIndexed { index, word ->
        if (index > 0) append(' ')
        val color = when {
            index < matchedWords -> correctColor
            index == mismatchWordIndex -> mismatchColor
            else -> defaultColor
        }
        withStyle(SpanStyle(color = color)) { append(word) }
    }
}

@Composable
private fun recognitionMessage(state: RecitationRecognitionState): Int? = when (state) {
    RecitationRecognitionState.Listening -> R.string.hifdh_listening
    RecitationRecognitionState.OfflineModelMissing -> R.string.hifdh_offline_model_required
    RecitationRecognitionState.OfflineArabicUnavailable -> R.string.hifdh_offline_asr_unavailable
    RecitationRecognitionState.PermissionRequired -> R.string.permission_microphone_required
    RecitationRecognitionState.NoSpeech -> R.string.hifdh_recitation_uncertain
    RecitationRecognitionState.OnlineServiceUnavailable -> R.string.hifdh_online_service_unavailable
    RecitationRecognitionState.NetworkUnavailable -> R.string.hifdh_online_network_unavailable
    RecitationRecognitionState.Error -> R.string.err_unknown
    else -> null
}

private fun OfflineModelFailure.messageResource(): Int = when (this) {
    OfflineModelFailure.NETWORK -> R.string.hifdh_model_download_network_error
    OfflineModelFailure.STORAGE -> R.string.hifdh_model_download_storage_error
    OfflineModelFailure.INVALID_MODEL -> R.string.hifdh_model_download_invalid_error
    OfflineModelFailure.UNKNOWN -> R.string.hifdh_model_download_unknown_error
}

@Composable
private fun RatingActions(viewModel: HifdhSessionViewModel) {
    Text(stringResource(R.string.hifdh_rate_prompt), style = MaterialTheme.typography.titleSmall)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        DifficultyRating.entries.forEach { rating ->
            OutlinedButton(onClick = { viewModel.rateCurrent(rating) }, modifier = Modifier.weight(1f).heightIn(min = 48.dp)) {
                Text(stringResource(rating.labelRes()))
            }
        }
    }
}

private fun progressiveAyah(text: String, level: Int): AnnotatedString {
    val words = text.split(' ')
    val hidePercent = when (level) {
        1 -> 0
        2 -> 25
        3 -> 50
        4 -> 75
        else -> 100
    }
    val hiddenCount = words.size * hidePercent / 100
    return buildAnnotatedString {
        words.forEachIndexed { index, word ->
            append(if (index < hiddenCount) "■".repeat(word.codePointCount(0, word.length).coerceIn(2, 8)) else word)
            if (index != words.lastIndex) append(' ')
        }
    }
}

private fun revealedAyahPart(text: String, part: Int): AnnotatedString {
    if (part == 0) return AnnotatedString("••••••••••••••••")
    val words = text.split(Regex("\\s+")).filter(String::isNotEmpty)
    val visibleCount = (words.size * part / 4).coerceAtLeast(1).coerceAtMost(words.size)
    return AnnotatedString(words.take(visibleCount).joinToString(" "))
}

private fun ayahMarker(number: Int): String =
    "\uFD3F" + number.toString().map { '\u0660' + (it - '0') }.joinToString("") + "\uFD3E"

private fun MemorizationStatus.labelRes(): Int = when (this) {
    MemorizationStatus.NEW -> R.string.hifdh_status_new
    MemorizationStatus.LEARNING -> R.string.hifdh_status_learning
    MemorizationStatus.REVIEW -> R.string.hifdh_status_review
    MemorizationStatus.MEMORIZED -> R.string.hifdh_status_memorized
}

private fun DifficultyRating.labelRes(): Int = when (this) {
    DifficultyRating.HARD -> R.string.hifdh_rate_hard
    DifficultyRating.MEDIUM -> R.string.hifdh_rate_medium
    DifficultyRating.EASY -> R.string.hifdh_rate_easy
}
