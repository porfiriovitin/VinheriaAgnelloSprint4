package br.com.fiap.vinheriaagnello.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Wine80,
    secondary = Grape80,
    tertiary = Gold80
)

private val LightColorScheme = lightColorScheme(
    primary = Wine40,
    secondary = Grape40,
    tertiary = Gold40,
    background = CellarBackground,
    surface = CellarSurface
)

/**
 * Applies the Vinheria Agnello Material theme to [content].
 *
 * @param darkTheme whether the dark color scheme should be used.
 * @param content composable content rendered inside the configured theme.
 */
@Composable
fun VinheriaAgnelloTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
