package com.quoren.app.feature.reader

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.core.content.ContextCompat
import com.quoren.app.R
import com.quoren.app.domain.model.QuranStructure
import com.quoren.app.domain.model.ReaderSettings
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun ReaderScreen(onBack: () -> Unit, viewModel: ReaderViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(initialPage = viewModel.startPage - 1) { QuranStructure.PAGE_COUNT }
    var showSettings by remember { mutableStateOf(false) }
    var showPlayback by remember { mutableStateOf(false) }
    var menuAyah by remember { mutableStateOf<AyahUi?>(null) }
    var liveFontSize by remember { mutableStateOf<Float?>(null) }
    var followAudio by rememberSaveable(viewModel.startPage) { mutableStateOf(false) }
    var pendingOnlineRecognition by remember { mutableStateOf(false) }

    val currentPage = pagerState.currentPage + 1
    val header by produceState<ReaderPageUi?>(null, currentPage) {
        value = runCatching { viewModel.loadPage(currentPage) }.getOrNull()
    }
    val current = state.currentAyah
    val context = LocalContext.current
    val requestMicPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            if (pendingOnlineRecognition) viewModel.startOnlineVoiceRecognition() else viewModel.startVoiceRecognitionWhenReady()
        } else {
            viewModel.setRecognitionPermissionDenied()
        }
        pendingOnlineRecognition = false
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }
            .debounce(400)
            .collect { viewModel.onPageSettled(it + 1) }
    }
    LaunchedEffect(pagerState) {
        pagerState.interactionSource.interactions.collect { if (it is DragInteraction.Start) followAudio = false }
    }
    LaunchedEffect(current?.ayahId, followAudio) {
        if (followAudio && current != null && pagerState.currentPage != current.page - 1) {
            pagerState.animateScrollToPage(current.page - 1)
        }
    }

    val fontSize = liveFontSize ?: state.settings.fontSizeSp
    val highlightId = if (followAudio) current?.ayahId ?: viewModel.highlightAyahId else viewModel.highlightAyahId
    val scrollTargetId = current?.ayahId?.takeIf { followAudio }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column {
                        Text(header?.title.orEmpty(), style = MaterialTheme.typography.titleMedium)
                        header?.let {
                            Text(
                                stringResource(
                                    R.string.juz_page_info,
                                    stringResource(R.string.juz_label, it.juz),
                                    stringResource(R.string.page_label, it.page),
                                ),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleVoicePractice(currentPage) }) {
                        Icon(
                            painterResource(R.drawable.ic_mic),
                            contentDescription = stringResource(R.string.reader_voice_practice),
                            tint = if (state.voicePractice.enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    IconButton(onClick = { showPlayback = true }) {
                        Icon(painterResource(R.drawable.ic_play), contentDescription = stringResource(R.string.action_play_audio))
                    }
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.action_settings))
                    }
                },
            )
        },
        bottomBar = {
            if (state.voicePractice.enabled) {
                ReaderVoiceControls(
                    state = state.voicePractice,
                    onToggle = { viewModel.toggleVoicePractice(currentPage) },
                    onRequestMicrophone = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                            viewModel.startVoiceRecognitionWhenReady()
                        } else {
                            requestMicPermission.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    },
                    onDownloadModel = viewModel::downloadVoiceModel,
                    onRequestOnline = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                            viewModel.startOnlineVoiceRecognition()
                        } else {
                            pendingOnlineRecognition = true
                            requestMicPermission.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    },
                    onFinish = viewModel::finishVoiceRecognition,
                    onCancel = viewModel::cancelVoiceRecognition,
                    onRevealPart = viewModel::revealNextVoicePart,
                )
            }
        },
        floatingActionButton = {
            if (state.currentAyah != null && !followAudio) {
                ExtendedFloatingActionButton(
                    onClick = { followAudio = true },
                    icon = { Icon(painterResource(R.drawable.ic_play), contentDescription = null) },
                    text = { Text(stringResource(R.string.return_to_current)) },
                )
            }
        },
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(padding),
            beyondViewportPageCount = 1,
            key = { it },
        ) { index ->
            ReaderPage(
                pageNumber = index + 1,
                loader = viewModel::loadPage,
                fontSizeSp = fontSize,
                lineSpacing = state.settings.lineSpacing,
                bookmarkedIds = state.bookmarkedIds,
                highlightAyahId = highlightId,
                scrollToAyahId = scrollTargetId,
                voicePractice = state.voicePractice,
                onAyahSelected = {
                    followAudio = false
                    menuAyah = it
                },
                onToggleBookmark = viewModel::onToggleBookmark,
                onZoom = { factor ->
                    liveFontSize = ((liveFontSize ?: state.settings.fontSizeSp) * factor)
                        .coerceIn(ReaderSettings.MIN_FONT_SIZE_SP, ReaderSettings.MAX_FONT_SIZE_SP)
                },
                onZoomEnd = {
                    liveFontSize?.let(viewModel::onFontSizeChanged)
                    liveFontSize = null
                },
                onUserScroll = { followAudio = false },
            )
        }
    }

    if (showSettings) {
        ReaderSettingsSheet(
            settings = state.settings,
            onFontSizeChanged = viewModel::onFontSizeChanged,
            onLineSpacingChanged = viewModel::onLineSpacingChanged,
            onThemeModeChanged = viewModel::onThemeModeChanged,
            onDismiss = { showSettings = false },
        )
    }

    if (showPlayback) {
        val surahId = header?.firstSurahId
        val ayahCount by produceState(0, surahId) { value = surahId?.let { runCatching { viewModel.ayahCount(it) }.getOrDefault(0) } ?: 0 }
        if (surahId != null && ayahCount > 0) {
            PlaybackSheet(
                ayahCount = ayahCount,
                followText = followAudio,
                onFollowTextChanged = { followAudio = it },
                onPlay = { start, end, reciterId ->
                    viewModel.playRange(surahId, start, end, reciterId)
                    showPlayback = false
                },
                onDismiss = { showPlayback = false },
            )
        }
    }

    menuAyah?.let { ayah ->
        ModalBottomSheet(onDismissRequest = { menuAyah = null }) {
            Column(Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 24.dp, vertical = 8.dp)) {
                Text(stringResource(R.string.ayah_menu_title, ayah.number), style = MaterialTheme.typography.titleMedium)
                MenuAction(R.string.menu_play_ayah) {
                    followAudio = true
                    viewModel.playAyah(ayah, repeat = false, toEndOfSurah = false)
                    menuAyah = null
                }
                MenuAction(R.string.menu_repeat_ayah) {
                    followAudio = true
                    viewModel.playAyah(ayah, repeat = true, toEndOfSurah = false)
                    menuAyah = null
                }
                MenuAction(if (ayah.id in state.bookmarkedIds) R.string.bookmark_remove else R.string.bookmark_add) {
                    viewModel.onToggleBookmark(ayah.id)
                    menuAyah = null
                }
            }
        }
    }
}

@Composable
private fun MenuAction(labelRes: Int, onClick: () -> Unit) {
    TextButton(onClick = onClick, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)) {
        Text(stringResource(labelRes), style = MaterialTheme.typography.bodyLarge)
    }
}
