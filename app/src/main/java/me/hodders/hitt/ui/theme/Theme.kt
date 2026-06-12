package me.hodders.hitt.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PhaseOrange,
    background = Background,
    surface = CardBackground,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    secondary = PhaseTeal
)

@Composable
fun HiitTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = HiitTypography,
        content = content
    )
}
