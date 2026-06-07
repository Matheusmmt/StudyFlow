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
import androidx.compose.ui.unit.dp
import com.example.studyflow.data.model.Reminder
import com.example.studyflow.ui.components.*
import com.example.studyflow.ui.viewmodel.SubjectViewModel
import java.util.Calendar

@Composable
fun RemindersTab(vm: SubjectViewModel, idDisciplina: Long) {
    val lembretes by vm.lembretes(idDisciplina).collectAsState(initial = emptyList())
    var mostrarDialogo by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        if (lembretes.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Sem lembretes") }
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(lembretes, key = { it.id }) { l ->
                    ElevatedCard {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = l.concluido,
                                onCheckedChange = { vm.salvarLembrete(l.copy(concluido = it)) })
                            Column(Modifier.weight(1f)) {
                                Text(l.titulo, style = MaterialTheme.typography.titleMedium)
                                Text(formatarDataHora(l.dataHora), style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            IconButton(onClick = { vm.excluirLembrete(l) }) { Icon(Icons.Default.Delete, null) }
                        }
                    }
                }
            }
        }
        FloatingActionButton(onClick = { mostrarDialogo = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)) {
            Icon(Icons.Default.Add, null)
        }
    }

    if (mostrarDialogo) {
        DialogoLembrete(idDisciplina, { mostrarDialogo = false }) {
            vm.salvarLembrete(it); mostrarDialogo = false
        }
    }
}

@Composable
private fun DialogoLembrete(idDisciplina: Long, aoFechar: () -> Unit, aoSalvar: (Reminder) -> Unit) {
    var titulo by remember { mutableStateOf("") }
    var dataMs by remember { mutableStateOf<Long?>(null) }
    var hora by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var mostrarData by remember { mutableStateOf(false) }
    var mostrarHora by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = aoFechar,
        title = { Text("Novo lembrete") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(titulo, { titulo = it }, label = { Text("Título") }, singleLine = true)
                OutlinedButton(onClick = { mostrarData = true }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.CalendarMonth, null); Spacer(Modifier.width(8.dp))
                    Text(dataMs?.let(::formatarData) ?: "Escolher data")
                }
                OutlinedButton(onClick = { mostrarHora = true }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Schedule, null); Spacer(Modifier.width(8.dp))
                    Text(hora?.let { "%02d:%02d".format(it.first, it.second) } ?: "Escolher hora")
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = titulo.isNotBlank() && dataMs != null && hora != null,
                onClick = {
                    val cal = Calendar.getInstance().apply {
                        timeInMillis = dataMs!!
                        set(Calendar.HOUR_OF_DAY, hora!!.first)
                        set(Calendar.MINUTE, hora!!.second)
                    }
                    aoSalvar(Reminder(disciplinaId = idDisciplina, titulo = titulo, dataHora = cal.timeInMillis))
                }
            ) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = aoFechar) { Text("Cancelar") } }
    )

    if (mostrarData) SeletorData({ dataMs = it }, { mostrarData = false })
    if (mostrarHora) SeletorHora({ h, m -> hora = h to m }, { mostrarHora = false })
}
