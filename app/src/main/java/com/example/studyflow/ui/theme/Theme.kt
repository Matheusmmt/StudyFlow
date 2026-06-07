package com.example.studyflow.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val EsquemaEscuro = darkColorScheme(
    primary = RoxoClaro, onPrimary = RoxoEscuro,
    background = FundoEscuro, surface = SuperficieEscura
)
private val EsquemaClaro = lightColorScheme(
    primary = RoxoPrimario, onPrimary = androidx.compose.ui.graphics.Color.White,
    background = FundoClaro, surface = androidx.compose.ui.graphics.Color.White
)

@Composable
fun StudyFlowTheme(
    escuro: Boolean = isSystemInDarkTheme(),
    coresDinamicas: Boolean = true,
    conteudo: @Composable () -> Unit
) {
    val esquema = when {
        coresDinamicas && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (escuro) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        escuro -> EsquemaEscuro
        else -> EsquemaClaro
    }
    MaterialTheme(colorScheme = esquema, typography = AppTypography, content = conteudo)
}
