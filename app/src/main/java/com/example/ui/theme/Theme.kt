package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = MetaBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8F2FF),
    onPrimaryContainer = Color(0xFF003087),
    secondary = MetaCyan,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F7FF),
    onSecondaryContainer = Color(0xFF004C6D),
    tertiary = MetaNavy,
    background = Color(0xFFFFFFFF),
    onBackground = TextPrimaryLight,
    surface = Color(0xFFFFFFFF),
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceSubtle,
    onSurfaceVariant = TextSecondaryLight,
    outline = SurfaceBorder,
    error = StatusSuspended,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = MetaCyan,
    onPrimary = MetaNavy,
    primaryContainer = Color(0xFF003D7A),
    onPrimaryContainer = Color(0xFFD6E4FF),
    secondary = MetaBlue,
    onSecondary = Color.White,
    tertiary = Color(0xFF90CAF9),
    background = SurfaceDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDarkCard,
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFF22314E),
    onSurfaceVariant = TextSecondaryDark,
    outline = SurfaceDarkBorder,
    error = Color(0xFFFF6B6B),
    onError = Color.White
)

@Composable
fun SufiSecurityTheme(
    darkTheme: Boolean = false, // Default to pristine light white design per reference design
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = Color.Transparent.toArgb()
                it.navigationBarColor = Color.Transparent.toArgb()
                val controller = WindowCompat.getInsetsController(it, view)
                controller.isAppearanceLightStatusBars = !darkTheme
                controller.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
