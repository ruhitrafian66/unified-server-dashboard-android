package com.unifiedserver.dashboard.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryVariant,
    onPrimaryContainer = Color(0xFFCDE8FF),
    secondary = Secondary,
    onSecondary = OnPrimary,
    secondaryContainer = Color(0xFF1C3A5A),
    onSecondaryContainer = Color(0xFFCDE8FF),
    tertiary = Tertiary,
    onTertiary = OnPrimary,
    tertiaryContainer = Color(0xFF3D2556),
    onTertiaryContainer = Color(0xFFEDD9FF),
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = Color(0xFF8B949E),
    outline = Color(0xFF30363D),
    outlineVariant = Color(0xFF21262D),
    error = StatusRed,
    onError = Color(0xFF1C0000),
    errorContainer = Color(0xFF5C1A1A),
    onErrorContainer = Color(0xFFFFB4AB),
    inverseSurface = Color(0xFFE6EDF3),
    inverseOnSurface = Color(0xFF0D1117),
    inversePrimary = Color(0xFF1F6FEB),
    surfaceTint = Primary,
    scrim = Color(0xFF000000),
)

@Composable
fun UnifiedServerDashboardTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
