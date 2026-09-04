package com.example.studyflow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** Ícone usado nos cards de disciplina. */
@Composable
fun IconeDisciplina(cor: Long, icone: String, tamanho: Int = 48) {
    val vetor: ImageVector = when (icone) {
        "code" -> Icons.Default.Code
        "science" -> Icons.Default.Science
        "history" -> Icons.Default.AccountBalance
        "psychology" -> Icons.Default.Psychology
        "calculate" -> Icons.Default.Functions
        else -> Icons.Default.MenuBook
    }
    Box(
        modifier = Modifier
            .size(tamanho.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(cor)),
        contentAlignment = Alignment.Center
    ) {
        Icon(vetor, contentDescription = null, tint = Color.White, modifier = Modifier.size((tamanho * 0.5).dp))
    }
}

/** Cartão base com o visual escuro arredondado. */
@Composable
fun CartaoSuperficie(
    modifier: Modifier = Modifier,
    conteudo: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(Modifier.padding(16.dp), content = conteudo)
    }
}

@Composable
fun EtiquetaFaltas(faltas: Int, maximo: Int) {
    val cor = when {
        maximo > 0 && faltas >= maximo -> MaterialTheme.colorScheme.error
        maximo > 0 && faltas >= maximo * 0.6 -> Color(0xFFFBBF24)
        faltas == 0 -> Color(0xFF4ADE80)
        else -> MaterialTheme.colorScheme.error
    }
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(cor.copy(alpha = 0.15f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = if (faltas == 1) "1 falta" else "$faltas faltas",
            color = cor,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun EstadoVazio(icone: ImageVector, titulo: String, descricao: String) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icone, null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            modifier = Modifier.size(56.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(titulo, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Text(
            descricao,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}