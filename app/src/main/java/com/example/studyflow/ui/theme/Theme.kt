package com.example.studyflow.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

// Formas e Arredondamentos Globais
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryAccent,
    onPrimary = TextOnPrimary,
    primaryContainer = PrimaryAccent.copy(alpha = 0.2f),
    onPrimaryContainer = PrimaryAccentLight,
    secondary = TextSecondary,
    onSecondary = TextPrimary,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = TextPrimary,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = AlertError,
    onError = TextPrimary,
    errorContainer = AlertErrorContainer,
    onErrorContainer = AlertError
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryAccent,
    onPrimary = TextOnPrimary,
    primaryContainer = PrimaryAccent.copy(alpha = 0.12f),
    onPrimaryContainer = PrimaryAccent,
    secondary = LightTextSecondary,
    onSecondary = LightTextPrimary,
    secondaryContainer = LightSurfaceVariant,
    onSecondaryContainer = LightTextPrimary,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    error = AlertError,
    onError = TextOnPrimary,
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = AlertError
)

@Composable
fun StudyFlowTheme(
    escuro: Boolean = isSystemInDarkTheme(),
    coresDinamicas: Boolean = false,
    conteudo: @Composable () -> Unit
) {
    val esquema = when {
        coresDinamicas && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (escuro) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        escuro -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = esquema.background.toArgb()
            window.navigationBarColor = esquema.background.toArgb()
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !escuro
            controller.isAppearanceLightNavigationBars = !escuro
        }
    }

    MaterialTheme(
        colorScheme = esquema,
        shapes = AppShapes,
        typography = AppTypography,
        content = conteudo
    )
}
