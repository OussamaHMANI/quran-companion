package com.quoren.app.feature.quran

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quoren.app.R
import com.quoren.app.core.designsystem.QuranFontFamily
import com.quoren.app.domain.model.RevelationType
import com.quoren.app.domain.model.Surah
import com.quoren.app.feature.bookmarks.BookmarksTab
import com.quoren.app.feature.prayer.PrayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranIndexScreen(
    onOpenReader: (page: Int, ayahId: Int) -> Unit,
    onOpenSearch: () -> Unit,
    onOpenDownloads: () -> Unit,
    onOpenAudioSettings: () -> Unit,
    onOpenMemorization: () -> Unit,
    onOpenPrayer: () -> Unit,
    onOpenQibla: () -> Unit,
    onOpenMosques: () -> Unit,
    initialTab: Int = 0,
    viewModel: QuranIndexViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val prayerViewModel: PrayerViewModel = hiltViewModel()
    val prayerState by prayerViewModel.state.collectAsStateWithLifecycle()
    var tab by rememberSaveable { mutableIntStateOf(initialTab) }
    LaunchedEffect(tab) {
        if (tab != 0) viewModel.load()
    }
    DisposableEffect(prayerViewModel) {
        prayerViewModel.startCountdownUpdates()
        onDispose { prayerViewModel.stopCountdownUpdates() }
    }
    val indexTabs = if (initialTab == 0) {
        listOf(0 to R.string.home_title, 1 to R.string.tab_surahs, 2 to R.string.tab_juz, 3 to R.string.tab_bookmarks)
    } else {
        listOf(1 to R.string.tab_surahs, 2 to R.string.tab_juz, 3 to R.string.tab_bookmarks)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onOpenSearch) {
                        Icon(Icons.Filled.Search, contentDescription = stringResource(R.string.action_search))
                    }
                },
            )
        },
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            TabRow(selectedTabIndex = indexTabs.indexOfFirst { it.first == tab }.coerceAtLeast(0)) {
                indexTabs.forEach { (tabValue, title) ->
                    Tab(
                        selected = tab == tabValue,
                        onClick = { tab = tabValue },
                        modifier = Modifier.heightIn(min = 48.dp),
                        text = { Text(stringResource(title)) },
                    )
                }
            }
            when {
                tab == 0 -> HomeDashboard(
                    state = state,
                    prayerState = prayerState,
                    onOpenReader = onOpenReader,
                    onOpenSearch = onOpenSearch,
                    onOpenDownloads = onOpenDownloads,
                    onOpenAudioSettings = onOpenAudioSettings,
                    onOpenMemorization = onOpenMemorization,
                    onOpenPrayer = onOpenPrayer,
                    onOpenQibla = onOpenQibla,
                    onOpenMosques = onOpenMosques,
                )
                state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                state.error -> Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(stringResource(R.string.load_error))
                    Button(onClick = viewModel::load, modifier = Modifier.padding(top = 16.dp)) {
                        Text(stringResource(R.string.action_retry))
                    }
                }
                tab == 1 -> SurahList(state.surahs) { onOpenReader(it.firstPage, 0) }
                tab == 2 -> JuzList(state.juz) { onOpenReader(it.page, it.ayahId) }
                else -> BookmarksTab(onOpen = { page, ayahId -> onOpenReader(page, ayahId) })
            }
        }
    }
}

@Composable
private fun SurahList(surahs: List<Surah>, onClick: (Surah) -> Unit) {
    LazyColumn(Modifier.fillMaxSize()) {
        items(surahs, key = { it.id }) { surah ->
            val type = stringResource(
                if (surah.revelationType == RevelationType.MECCAN) R.string.meccan else R.string.medinan,
            )
            val info = stringResource(
                R.string.surah_page_info,
                type,
                stringResource(R.string.ayah_count, surah.numberOfAyahs),
                stringResource(R.string.page_label, surah.firstPage),
            )
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { onClick(surah) }
                    .heightIn(min = 64.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .semantics(mergeDescendants = true) {},
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                NumberBadge(surah.id)
                Column(Modifier.weight(1f)) {
                    Text(surah.nameArabic, style = MaterialTheme.typography.titleLarge, fontFamily = QuranFontFamily)
                    Text(info, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            HorizontalDivider()
        }
        item(key = "attribution") {
            val uriHandler = LocalUriHandler.current
            TextButton(
                onClick = { uriHandler.openUri("https://tanzil.net") },
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
            ) {
                Text(stringResource(R.string.source_attribution), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun JuzList(items: List<JuzItem>, onClick: (JuzItem) -> Unit) {
    LazyColumn(Modifier.fillMaxSize()) {
        items(items, key = { it.juz }) { item ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { onClick(item) }
                    .heightIn(min = 64.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .semantics(mergeDescendants = true) {},
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                NumberBadge(item.juz)
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.juz_label, item.juz), style = MaterialTheme.typography.titleMedium)
                    Text(
                        stringResource(R.string.ayah_ref, item.surahName, item.ayahNumber) + " · " +
                            stringResource(R.string.page_label, item.page),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            HorizontalDivider()
        }
    }
}

@Composable
private fun NumberBadge(number: Int) {
    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(40.dp)) {
        Box(contentAlignment = Alignment.Center) {
            Text(number.toString(), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}
