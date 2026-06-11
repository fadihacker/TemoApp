package com.eloop.mobileapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color.White,
    primaryContainer = PrimaryGreen.copy(alpha = 0.1f),
    onPrimaryContainer = PrimaryDarkGreen,
    secondary = PrimaryDarkGreen,
    onSecondary = Color.White,
    background = BackgroundGreen,
    onBackground = TextMain,
    surface = SurfaceWhite,
    onSurface = TextMain,
    surfaceVariant = Color(0xFFE8EDE7),
    onSurfaceVariant = TextMuted,
    outline = BorderColor,
    error = DangerRed,
    onError = Color.White,
)

private val DarkColors = darkColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color.Black,
    primaryContainer = PrimaryGreen.copy(alpha = 0.2f),
    onPrimaryContainer = Color.White,
    secondary = PrimaryDarkGreen,
    onSecondary = Color.White,
    background = Color(0xFF1A1C19),
    onBackground = Color(0xFFE2E3DD),
    surface = Color(0xFF222521),
    onSurface = Color(0xFFE2E3DD),
    surfaceVariant = Color(0xFF424940),
    onSurfaceVariant = Color(0xFFC2C8BC),
    outline = Color(0xFF8C9388),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
)

@Composable
fun ELoopTheme(
    themeMode: String = "SYSTEM",
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        "LIGHT" -> false
        "DARK" -> true
        else -> isSystemInDarkTheme()
    }
    
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ELoopTypography,
        content = content
    )
}


