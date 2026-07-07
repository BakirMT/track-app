package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkColorScheme = darkColorScheme(
    primary = AppGreen500,
    onPrimary = Color.White,
    primaryContainer = Slate800,
    onPrimaryContainer = AppGreen400,
    secondary = Emerald500,
    onSecondary = Color.White,
    secondaryContainer = Slate800,
    onSecondaryContainer = Emerald400,
    tertiary = Rose500,
    onTertiary = Color.White,
    tertiaryContainer = Slate800,
    onTertiaryContainer = Rose400,
    error = Rose500,
    onError = Color.White,
    background = Slate950,
    onBackground = Slate100,
    surface = Slate900,
    onSurface = Slate100,
    surfaceVariant = Slate900,
    onSurfaceVariant = Slate400,
    outline = Slate800,
    outlineVariant = Slate800
)

val LightColorScheme = lightColorScheme(
    primary = AppGreen600,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1FAE5), // Green100
    onPrimaryContainer = Color(0xFF064E3B), // Green900
    secondary = Color(0xFF059669), // Emerald600
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD1FAE5), // Emerald100
    onSecondaryContainer = Emerald900,
    tertiary = Color(0xFFE11D48), // Rose600
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE4E6), // Rose100
    onTertiaryContainer = Color(0xFF881337), // Rose900
    error = Color(0xFFE11D48), // Rose600
    onError = Color.White,
    background = Color(0xFFF4F8F5), // Slate50
    onBackground = Color(0xFF131F19), // Slate900
    surface = Color.White,
    onSurface = Color(0xFF131F19), // Slate900
    surfaceVariant = Color(0xFFE4EDE7), // Slate100
    onSurfaceVariant = Color(0xFF25362C), // Slate800
    outline = Color(0xFFBBCFC3), // Slate300
    outlineVariant = Color(0xFFD2E3D8) // Lighter outline
)

val GlassColorScheme = darkColorScheme(
    primary = Color(0xCCFFFFFF),
    onPrimary = Color.Black,
    primaryContainer = Color(0x33FFFFFF),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0x99FFFFFF),
    onSecondary = Color.Black,
    secondaryContainer = Color(0x22FFFFFF),
    onSecondaryContainer = Color(0xFFFFFFFF),
    tertiary = Color(0x66FFFFFF),
    onTertiary = Color.Black,
    tertiaryContainer = Color(0x11FFFFFF),
    onTertiaryContainer = Color(0xFFFFFFFF),
    error = Color(0xFFFF8A80),
    onError = Color.Black,
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0x1AFFFFFF), // Glassy surface
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0x0DFFFFFF),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0x33FFFFFF),
    outlineVariant = Color(0x1AFFFFFF)
)

enum class ThemeOption {
    DARK, LIGHT, GLASS
}

@Composable
fun MyApplicationTheme(
    themeOption: ThemeOption = ThemeOption.DARK,
    content: @Composable () -> Unit,
) {
    val colorScheme = when (themeOption) {
        ThemeOption.DARK -> DarkColorScheme
        ThemeOption.LIGHT -> LightColorScheme
        ThemeOption.GLASS -> GlassColorScheme
    }
    
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
