package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E3A8A),
    secondary = GoldDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = EmeraldGreen,
    onTertiary = Color.White,
    background = Color(0xFFF1F5F9),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF8FAFC),
    onSurfaceVariant = Color(0xFF334155),
    outline = GrayBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = BlueLight,
    onPrimary = NavyDark,
    primaryContainer = NavyLight,
    onPrimaryContainer = Color(0xFF93C5FD),
    secondary = GoldAccent,
    onSecondary = NavyDark,
    secondaryContainer = Color(0xFF451A03),
    onSecondaryContainer = Color(0xFFFDE68A),
    tertiary = EmeraldGreen,
    onTertiary = NavyDark,
    background = NavyDark,
    onBackground = Color(0xFFF8FAFC),
    surface = NavySurface,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = NavyLight,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF475569)
)

@Composable
fun MyApplicationTheme(
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
