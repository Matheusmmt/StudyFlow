package com.example.studyflow.ui.screens.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.studyflow.ui.components.SeletorData
import com.example.studyflow.ui.components.formatarData
import com.example.studyflow.ui.viewmodel.SubjectViewModel

@Composable
fun AbsencesTab(vm: SubjectViewModel, idDisciplina: Long, maxFaltas: Int) {
    val faltas by vm.faltas(idDisciplina).collectAsState(initial = emptyList())
    val total by vm.totalFaltas(idDisciplina).collectAsState(initial = 0)
    var mostrarSeletor by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ElevatedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp)) {
                Text("Contador de Faltas", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("$total", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold,
                        color = if (total >= maxFaltas) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                    Text(" / $maxFaltas", style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                LinearProgressIndicator(
                    progress = { (total.toFloat() / maxFaltas).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                )
                if (total >= maxFaltas)
                    Text("⚠️ Limite atingido!", color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp))
            }
        }

        Button(onClick = { mostrarSeletor = true }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Add, null); Spacer(Modifier.width(8.dp)); Text("Registrar falta")
        }

        Text("Histórico", style = MaterialTheme.typography.titleMedium)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(faltas, key = { it.id }) { f ->
                ListItem(
                    headlineContent = { Text(formatarData(f.data)) },
                    supportingContent = { if (f.motivo.isNotBlank()) Text(f.motivo) },
                    leadingContent = { Icon(Icons.Default.EventBusy, null) },
                    trailingContent = {
                        IconButton(onClick = { vm.excluirFalta(f) }) { Icon(Icons.Default.Delete, null) }
                    }
                )
            }
        }
    }

    if (mostrarSeletor) {
        SeletorData(
            aoSelecionar = { vm.adicionarFalta(idDisciplina, it) },
            aoFechar = { mostrarSeletor = false }
        )
    }
}
