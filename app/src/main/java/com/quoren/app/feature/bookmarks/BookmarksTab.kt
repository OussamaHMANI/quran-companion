package com.quoren.app.feature.bookmarks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quoren.app.R
import com.quoren.app.core.designsystem.QuranFontFamily

@Composable
fun BookmarksTab(onOpen: (page: Int, ayahId: Int) -> Unit, viewModel: BookmarksViewModel = hiltViewModel()) {
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    val list = bookmarks ?: return
    if (list.isEmpty()) {
        Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.no_bookmarks), style = MaterialTheme.typography.bodyLarge)
        }
        return
    }
    LazyColumn(Modifier.fillMaxSize()) {
        items(list, key = { it.bookmark.id }) { item ->
            Column(
                Modifier
                    .fillMaxWidth()
                    .clickable { onOpen(item.ayah.page, item.ayah.id) }
                    .heightIn(min = 64.dp)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .semantics(mergeDescendants = true) {},
            ) {
                Text(
                    stringResource(R.string.ayah_ref, item.surah.nameArabic, item.ayah.ayahNumber),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    item.ayah.textUthmani,
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = QuranFontFamily,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            HorizontalDivider()
        }
    }
}
