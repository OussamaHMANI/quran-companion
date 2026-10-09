package com.quoren.app.feature.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.quoren.app.R
import com.quoren.app.core.designsystem.QuranFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onOpenAyah: (page: Int, ayahId: Int) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val query by viewModel.currentQuery.collectAsStateWithLifecycle()
    val focus = remember { FocusRequester() }
    LaunchedEffect(Unit) { focus.requestFocus() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = query,
                        onValueChange = viewModel::onQueryChanged,
                        placeholder = { Text(stringResource(R.string.search_hint)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().focusRequester(focus),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                },
            )
        },
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when {
                state.searching -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                query.trim().length < 2 -> Text(
                    stringResource(R.string.search_min_chars),
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                state.results.isEmpty() -> Text(
                    stringResource(R.string.search_empty),
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                )
                else -> LazyColumn(Modifier.fillMaxSize()) {
                    items(state.results, key = { it.ayah.id }) { result ->
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .clickable { onOpenAyah(result.ayah.page, result.ayah.id) }
                                .heightIn(min = 64.dp)
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .semantics(mergeDescendants = true) {},
                        ) {
                            Text(
                                stringResource(R.string.ayah_ref, result.surah.nameArabic, result.ayah.ayahNumber),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Text(
                                result.ayah.textUthmani,
                                style = MaterialTheme.typography.titleLarge,
                                fontFamily = QuranFontFamily,
                            )
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
