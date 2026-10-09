package com.quoren.app.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.quoren.app.R
import com.quoren.app.domain.model.ThemeMode

val QuranPrimary = Color(0xFF086B5A)
val QuranBackground = Color(0xFFF5F6F1)
val QuranDark = Color(0xFF10221F)
val QuranAccent = Color(0xFFC89548)
private val QuranCopper = Color(0xFFB65F47)

val QuranFontFamily = FontFamily(Font(R.font.amiri_quran))

private val LightColors = lightColorScheme(
    primary = QuranPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD7EDE5),
    onPrimaryContainer = QuranDark,
    secondary = QuranCopper,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3E2D9),
    onSecondaryContainer = Color(0xFF452016),
    tertiary = QuranAccent,
    onTertiary = QuranDark,
    tertiaryContainer = Color(0xFFF2E6C9),
    onTertiaryContainer = Color(0xFF35270E),
    background = QuranBackground,
    onBackground = QuranDark,
    surface = Color(0xFFFFFEFA),
    onSurface = QuranDark,
    surfaceVariant = Color(0xFFE9EEEA),
    onSurfaceVariant = Color(0xFF4D5B56),
    outline = Color(0xFF84918B),
    outlineVariant = Color(0xFFD6DED8),
    error = Color(0xFFB3261E),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF7BD2B8),
    onPrimary = QuranDark,
    primaryContainer = Color(0xFF174A3D),
    onPrimaryContainer = Color(0xFFD7EDE5),
    secondary = Color(0xFFE09A7B),
    onSecondary = Color(0xFF35170F),
    secondaryContainer = Color(0xFF593126),
    onSecondaryContainer = Color(0xFFFFDBCC),
    tertiary = QuranAccent,
    onTertiary = QuranDark,
    tertiaryContainer = Color(0xFF514222),
    onTertiaryContainer = Color(0xFFF2E6C9),
    background = QuranDark,
    onBackground = Color(0xFFEDEAE0),
    surface = Color(0xFF132723),
    onSurface = Color(0xFFEDEAE0),
    surfaceVariant = Color(0xFF1D332D),
    onSurfaceVariant = Color(0xFFBBC8C1),
    outline = Color(0xFF879991),
    outlineVariant = Color(0xFF344A42),
    error = Color(0xFFFFB4AB),
)

private val MishkatShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

@Composable
fun QuoranTheme(themeMode: ThemeMode, content: @Composable () -> Unit) {
    val dark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = if (dark) DarkColors else LightColors,
            shapes = MishkatShapes,
            content = content,
        )
    }
}
