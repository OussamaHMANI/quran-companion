package com.quoren.app.feature.quran

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.quoren.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    onOpenSearch: () -> Unit,
    onOpenDownloads: () -> Unit,
    onOpenAudioSettings: () -> Unit,
    onOpenQibla: () -> Unit,
    onOpenMosques: () -> Unit,
) {
    val actions = listOf(
        MoreItem(R.string.action_search, Icons.Filled.Search, onOpenSearch),
        MoreItem(R.string.action_downloads, Icons.Filled.PlayArrow, onOpenDownloads),
        MoreItem(R.string.action_reciters, Icons.Filled.Person, onOpenAudioSettings),
        MoreItem(R.string.qibla_title, Icons.Filled.LocationOn, onOpenQibla),
        MoreItem(R.string.mosque_title, Icons.Filled.LocationOn, onOpenMosques),
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.more_title)) }) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(vertical = 12.dp),
        ) {
            item {
                Text(
                    text = stringResource(R.string.more_tools),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                )
            }
            items(actions) { action ->
                ListItem(
                    headlineContent = { Text(stringResource(action.title)) },
                    leadingContent = { Icon(action.icon, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().clickable(onClick = action.onClick),
                )
                HorizontalDivider()
            }
        }
    }
}

private data class MoreItem(
    val title: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val onClick: () -> Unit,
)
