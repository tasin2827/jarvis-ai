package com.jarvis.ai.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val JarvisColorScheme = darkColorScheme(
    primary = JarvisCyanPrimary,
    onPrimary = JarvisObsidian,
    primaryContainer = JarvisDarkNavy,
    onPrimaryContainer = JarvisCyanPrimary,
    secondary = JarvisElectricBlue,
    onSecondary = JarvisObsidian,
    background = JarvisObsidian,
    onBackground = JarvisTextPrimary,
    surface = JarvisSurfaceCard,
    onSurface = JarvisTextPrimary,
    surfaceVariant = JarvisSurfaceBorder,
    onSurfaceVariant = JarvisTextSecondary,
    error = JarvisAlertRed,
    onError = JarvisTextPrimary
)

@Composable
fun JarvisAITheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = JarvisObsidian.toArgb()
                it.navigationBarColor = JarvisObsidian.toArgb()
                WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = JarvisColorScheme,
        content = content
    )
}
