package com.quoren.app.feature.reader

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.quoren.app.R
import com.quoren.app.domain.model.ReaderSettings
import com.quoren.app.domain.model.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderSettingsSheet(
    settings: ReaderSettings,
    onFontSizeChanged: (Float) -> Unit,
    onLineSpacingChanged: (Float) -> Unit,
    onThemeModeChanged: (ThemeMode) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(stringResource(R.string.font_size), style = MaterialTheme.typography.titleMedium)
            Slider(
                value = settings.fontSizeSp,
                onValueChange = onFontSizeChanged,
                valueRange = ReaderSettings.MIN_FONT_SIZE_SP..ReaderSettings.MAX_FONT_SIZE_SP,
            )
            Text(stringResource(R.string.line_spacing), style = MaterialTheme.typography.titleMedium)
            Slider(
                value = settings.lineSpacing,
                onValueChange = onLineSpacingChanged,
                valueRange = ReaderSettings.MIN_LINE_SPACING..ReaderSettings.MAX_LINE_SPACING,
            )
            Text(stringResource(R.string.theme), style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemeChip(ThemeMode.SYSTEM, R.string.theme_system, settings.themeMode, onThemeModeChanged)
                ThemeChip(ThemeMode.LIGHT, R.string.theme_light, settings.themeMode, onThemeModeChanged)
                ThemeChip(ThemeMode.DARK, R.string.theme_dark, settings.themeMode, onThemeModeChanged)
            }
            TextButton(onClick = onDismiss, modifier = Modifier.heightIn(min = 48.dp)) {
                Text(stringResource(R.string.action_close))
            }
        }
    }
}

@Composable
private fun ThemeChip(mode: ThemeMode, label: Int, selected: ThemeMode, onSelect: (ThemeMode) -> Unit) {
    FilterChip(
        selected = mode == selected,
        onClick = { onSelect(mode) },
        label = { Text(stringResource(label)) },
    )
}
