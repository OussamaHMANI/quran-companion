package com.quoren.app.feature.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quoren.app.R
import com.quoren.app.core.designsystem.QuranAccent
import com.quoren.app.core.designsystem.QuranFontFamily
import kotlinx.coroutines.CancellationException

@Composable
fun ReaderPage(
    pageNumber: Int,
    loader: suspend (Int) -> ReaderPageUi,
    fontSizeSp: Float,
    lineSpacing: Float,
    bookmarkedIds: Set<Int>,
    highlightAyahId: Int?,
    scrollToAyahId: Int?,
    voicePractice: ReaderVoicePracticeState,
    onAyahSelected: (AyahUi) -> Unit,
    onToggleBookmark: (Int) -> Unit,
    onZoom: (Float) -> Unit,
    onZoomEnd: () -> Unit,
    onUserScroll: () -> Unit,
) {
    var data by remember(pageNumber) { mutableStateOf<ReaderPageUi?>(null) }
    var failed by remember(pageNumber) { mutableStateOf(false) }
    var attempt by remember(pageNumber) { mutableIntStateOf(0) }

    LaunchedEffect(pageNumber, attempt) {
        failed = false
        try {
            data = loader(pageNumber)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            failed = true
        }
    }

    val page = data
    when {
        page != null && voicePractice.enabled && voicePractice.currentPage == page.page -> VoicePracticeReaderPage(
            page = page,
            voicePractice = voicePractice,
            fontSizeSp = fontSizeSp,
            onUserScroll = onUserScroll,
        )
        page != null -> ReaderPageContent(
            page = page,
            fontSizeSp = fontSizeSp,
            lineSpacing = lineSpacing,
            bookmarkedIds = bookmarkedIds,
            highlightAyahId = highlightAyahId,
            scrollToAyahId = scrollToAyahId,
            onAyahSelected = onAyahSelected,
            onToggleBookmark = onToggleBookmark,
            onZoom = onZoom,
            onZoomEnd = onZoomEnd,
            onUserScroll = onUserScroll,
        )
        failed -> Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(stringResource(R.string.load_error), style = MaterialTheme.typography.bodyLarge)
            Button(onClick = { attempt++ }, modifier = Modifier.padding(top = 16.dp)) {
                Text(stringResource(R.string.action_retry))
            }
        }
        else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            val loadingText = stringResource(R.string.loading)
            CircularProgressIndicator(Modifier.semantics { contentDescription = loadingText })
        }
    }
}

@Composable
private fun VoicePracticeReaderPage(
    page: ReaderPageUi,
    voicePractice: ReaderVoicePracticeState,
    fontSizeSp: Float,
    onUserScroll: () -> Unit,
) {
    val ayahs = page.blocks.filterIsInstance<PageBlock.Ayahs>().flatMap { it.items }
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()
    LaunchedEffect(listState) {
        listState.interactionSource.interactions.collect { if (it is DragInteraction.Start) onUserScroll() }
    }
    LaunchedEffect(voicePractice.currentAyahId, ayahs) {
        val index = ayahs.indexOfFirst { it.id == voicePractice.currentAyahId }
        if (index >= 0) listState.animateScrollToItem(index + 1)
    }
    LazyColumn(
        Modifier.fillMaxSize().pinchToZoom({}, {}).padding(horizontal = 20.dp),
        state = listState,
    ) {
        item(key = "voice-header") {
            Text(
                page.title,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                textAlign = TextAlign.Center,
                fontFamily = QuranFontFamily,
                fontSize = (fontSizeSp * 0.8f).sp,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        items(ayahs, key = { it.id }) { ayah ->
            val isCurrent = ayah.id == voicePractice.currentAyahId
            val revealed = if (isCurrent && voicePractice.revealedPart > 0) {
                partialCanonicalText(ayah.text, voicePractice.revealedPart)
            } else {
                ""
            }
            Column(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 6.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Text(
                        ayahMarker(ayah.number),
                        color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = QuranFontFamily,
                        fontSize = (fontSizeSp * 0.75f).sp,
                    )
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f).padding(bottom = 4.dp)) {
                        if (isCurrent && voicePractice.transcript.isNotBlank()) {
                            Text(buildVoiceTranscript(
                                transcript = voicePractice.transcript,
                                matchedWords = voicePractice.matchedWords,
                                mismatchWordIndex = voicePractice.mismatchWordIndex,
                                correctColor = MaterialTheme.colorScheme.primary,
                                mismatchColor = MaterialTheme.colorScheme.error,
                                defaultColor = MaterialTheme.colorScheme.onBackground,
                            ),
                                fontFamily = QuranFontFamily,
                                fontSize = fontSizeSp.sp,
                                lineHeight = (fontSizeSp * 1.45f).sp,
                                textAlign = TextAlign.Start,
                            )
                        }
                        if (isCurrent && revealed.isNotBlank()) {
                            Text(
                                revealed,
                                fontFamily = QuranFontFamily,
                                fontSize = fontSizeSp.sp,
                                lineHeight = (fontSizeSp * 1.45f).sp,
                                textAlign = TextAlign.Start,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                        if (ayah.id in voicePractice.completedAyahIds) {
                            Text(
                                ayah.text,
                                fontFamily = QuranFontFamily,
                                fontSize = fontSizeSp.sp,
                                lineHeight = (fontSizeSp * 1.45f).sp,
                                textAlign = TextAlign.Start,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                        if (!isCurrent && ayah.id !in voicePractice.completedAyahIds) {
                            Spacer(Modifier.fillMaxWidth().heightIn(min = 48.dp))
                        } else if (isCurrent && voicePractice.transcript.isBlank() && revealed.isBlank() &&
                            ayah.id !in voicePractice.completedAyahIds
                        ) {
                            Spacer(Modifier.fillMaxWidth().heightIn(min = 48.dp))
                        }
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
            }
        }
        item(key = "voice-page-number") {
            Text(
                stringResource(R.string.page_label, page.page),
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
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

private fun partialCanonicalText(text: String, part: Int): String {
    val words = text.split(Regex("\\s+")).filter(String::isNotEmpty)
    if (words.isEmpty()) return ""
    val visibleCount = (words.size * part / 4).coerceIn(1, words.size)
    return words.take(visibleCount).joinToString(" ")
}

@Composable
private fun ReaderPageContent(
    page: ReaderPageUi,
    fontSizeSp: Float,
    lineSpacing: Float,
    bookmarkedIds: Set<Int>,
    highlightAyahId: Int?,
    scrollToAyahId: Int?,
    onAyahSelected: (AyahUi) -> Unit,
    onToggleBookmark: (Int) -> Unit,
    onZoom: (Float) -> Unit,
    onZoomEnd: () -> Unit,
    onUserScroll: () -> Unit,
) {
    val textStyle = remember(fontSizeSp, lineSpacing) {
        TextStyle(
            fontFamily = QuranFontFamily,
            fontSize = fontSizeSp.sp,
            lineHeight = (fontSizeSp * lineSpacing).sp,
            textAlign = TextAlign.Justify,
            textDirection = TextDirection.Rtl,
        )
    }
    val scrollState = rememberScrollState()
    var viewportHeight by remember { mutableIntStateOf(0) }
    LaunchedEffect(scrollState) {
        scrollState.interactionSource.interactions.collect { if (it is DragInteraction.Start) onUserScroll() }
    }
    Column(
        Modifier
            .fillMaxSize()
            .onSizeChanged { viewportHeight = it.height }
            .pinchToZoom(onZoom, onZoomEnd)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        page.blocks.forEach { block ->
            when (block) {
                is PageBlock.SurahHeader -> SurahHeader(block, fontSizeSp)
                is PageBlock.Ayahs -> {
                    val ids = block.items.first().id..block.items.last().id
                    AyahsText(
                        items = block.items,
                        style = textStyle,
                        bookmarkedIds = bookmarkedIds,
                        highlightAyahId = highlightAyahId?.takeIf { it in ids },
                        scrollToAyahId = scrollToAyahId?.takeIf { it in ids },
                        scrollState = scrollState,
                        viewportHeight = viewportHeight,
                        onAyahSelected = onAyahSelected,
                        onToggleBookmark = onToggleBookmark,
                    )
                }
            }
        }
        Text(
            text = stringResource(R.string.page_label, page.page),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SurahHeader(block: PageBlock.SurahHeader, fontSizeSp: Float) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .border(1.dp, QuranAccent, RoundedCornerShape(12.dp))
            .padding(12.dp)
            .semantics(mergeDescendants = true) { heading() },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.surah_header, block.name),
            style = TextStyle(fontFamily = QuranFontFamily, fontSize = (fontSizeSp * 0.9f).sp),
            color = MaterialTheme.colorScheme.primary,
        )
        block.basmala?.let {
            Text(
                text = it,
                style = TextStyle(
                    fontFamily = QuranFontFamily,
                    fontSize = (fontSizeSp * 0.85f).sp,
                    textDirection = TextDirection.Rtl,
                ),
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

private class BuiltAyahText(val text: AnnotatedString, val ranges: List<IntRange>) {
    fun indexAt(offset: Int): Int = ranges.indexOfFirst { offset in it }
}

@Composable
private fun AyahsText(
    items: List<AyahUi>,
    style: TextStyle,
    bookmarkedIds: Set<Int>,
    highlightAyahId: Int?,
    scrollToAyahId: Int?,
    scrollState: ScrollState,
    viewportHeight: Int,
    onAyahSelected: (AyahUi) -> Unit,
    onToggleBookmark: (Int) -> Unit,
) {
    val markerColor = MaterialTheme.colorScheme.primary
    val bookmarkColor = MaterialTheme.colorScheme.secondary
    val highlightColor = MaterialTheme.colorScheme.primaryContainer
    val built = remember(items, bookmarkedIds, highlightAyahId, markerColor, bookmarkColor, highlightColor) {
        buildAyahText(items, bookmarkedIds, highlightAyahId, markerColor, bookmarkColor, highlightColor)
    }
    var layout by remember { mutableStateOf<TextLayoutResult?>(null) }
    var blockTop by remember { mutableFloatStateOf(0f) }
    val resources = LocalContext.current.resources

    // Scroll only when the ayah leaves the comfortable band, so following the recitation never jitters.
    LaunchedEffect(scrollToAyahId, layout, viewportHeight) {
        val id = scrollToAyahId ?: return@LaunchedEffect
        val result = layout ?: return@LaunchedEffect
        val index = items.indexOfFirst { it.id == id }
        if (index < 0 || viewportHeight == 0) return@LaunchedEffect
        val top = blockTop + result.getBoundingBox(built.ranges[index].first).top
        val visibleTop = scrollState.value
        if (top < visibleTop || top > visibleTop + viewportHeight * 0.6f) {
            scrollState.animateScrollTo((top - viewportHeight * 0.2f).toInt().coerceIn(0, scrollState.maxValue))
        }
    }

    Text(
        text = built.text,
        style = style,
        onTextLayout = { layout = it },
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { blockTop = it.positionInParent().y }
            .pointerInput(built, items) {
                detectTapGestures(
                    onTap = { position ->
                        val result = layout ?: return@detectTapGestures
                        val index = built.indexAt(result.getOffsetForPosition(position))
                        if (index >= 0) onAyahSelected(items[index])
                    },
                    onLongPress = { position ->
                        val result = layout ?: return@detectTapGestures
                        val index = built.indexAt(result.getOffsetForPosition(position))
                        if (index >= 0) onAyahSelected(items[index])
                    },
                )
            }
            .semantics {
                customActions = items.map { ayah ->
                    val label = if (ayah.id in bookmarkedIds) {
                        resources.getString(R.string.bookmark_remove_ayah, ayah.number)
                    } else {
                        resources.getString(R.string.bookmark_add_ayah, ayah.number)
                    }
                    CustomAccessibilityAction(label) { onToggleBookmark(ayah.id); true }
                }
            },
    )
}

private fun buildAyahText(
    items: List<AyahUi>,
    bookmarkedIds: Set<Int>,
    highlightAyahId: Int?,
    markerColor: androidx.compose.ui.graphics.Color,
    bookmarkColor: androidx.compose.ui.graphics.Color,
    highlightColor: androidx.compose.ui.graphics.Color,
): BuiltAyahText {
    val ranges = ArrayList<IntRange>(items.size)
    val text = buildAnnotatedString {
        for (ayah in items) {
            val start = length
            val highlight = if (ayah.id == highlightAyahId) SpanStyle(background = highlightColor) else SpanStyle()
            withStyle(highlight) { append(ayah.text) }
            append(' ')
            val color = if (ayah.id in bookmarkedIds) bookmarkColor else markerColor
            withStyle(highlight.merge(SpanStyle(color = color))) { append(ayahMarker(ayah.number)) }
            append(' ')
            ranges += start until length
        }
    }
    return BuiltAyahText(text, ranges)
}

/** UI-only end-of-ayah ornament; not part of the canonical text. */
private fun ayahMarker(number: Int): String {
    val digits = number.toString().map { '\u0660' + (it - '0') }.joinToString("")
    return "\uFD3F$digits\uFD3E"
}
