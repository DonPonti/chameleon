package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = GreenPrimary,
    secondary = GreenSecondary,
    tertiary = TealTertiary,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = Color(0xFF021E14),
    onSecondary = Color(0xFF021E14),
    onTertiary = Color(0xFF001B1A),
    onBackground = TextLight,
    onSurface = TextLight,
    surfaceVariant = Color(0xFF1B362F),
    onSurfaceVariant = TextLight
  )

private val LightColorScheme =
  lightColorScheme(
    primary = LightPrimary,
    secondary = LightSecondary,
    tertiary = LightTertiary,
    background = Color(0xFFF8FAFC),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark theme by default for premium camouflage game vibe!
  dynamicColor: Boolean = false, // Disable dynamic colors to enforce branding Chameleon palette
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
