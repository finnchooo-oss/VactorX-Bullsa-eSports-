package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PremiumDarkColorScheme = darkColorScheme(
    primary = GamingRed,
    onPrimary = White,
    primaryContainer = GamingRed,
    onPrimaryContainer = White,
    secondary = MetallicGold,
    onSecondary = Black,
    secondaryContainer = DarkGold,
    onSecondaryContainer = White,
    tertiary = NeonRed,
    onTertiary = White,
    background = JetBlack,
    onBackground = TextPrimary,
    surface = CardBackground,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceBorder,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceBorder
)

// We maintain a consistent dark theme as per esports standards, 
// but define a light scheme just in case the system requests it (with gold/red accents on light grey)
private val PremiumLightColorScheme = lightColorScheme(
    primary = GamingRed,
    onPrimary = White,
    secondary = MetallicGold,
    onSecondary = Black,
    background = Color(0xFFF9F9F9),
    onBackground = Black,
    surface = Color(0xFFFFFFFF),
    onSurface = Black,
    outline = Color(0xFFE0E0E0)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force dark theme by default for esports visual style
    dynamicColor: Boolean = false, // Disable dynamic colors to keep brand's exact colors
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) PremiumDarkColorScheme else PremiumLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
