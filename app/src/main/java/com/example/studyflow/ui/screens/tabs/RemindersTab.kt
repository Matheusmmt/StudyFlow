package com.example.studyflow.ui.screens.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studyflow.data.model.Reminder
import com.example.studyflow.ui.components.*
import com.example.studyflow.ui.viewmodel.SubjectDetailViewModel
import java.util.Calendar

@Composable
fun RemindersTab(vm: SubjectDetailViewModel) {
    val lembretes by vm.lembretes.collectAsStateWithLifecycle()
    var mostrarDialogo by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        if (lembretes.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "Sem lembretes de estudo agendados.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(lembretes, key = { it.id }) { lembrete ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 0.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = lembrete.concluido,
                                onCheckedChange = { vm.salvarLembrete(lembrete.copy(concluido = it)) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MaterialTheme.colorScheme.primary,
                                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Spacer(Modifier.width(8.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    lembrete.titulo,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    formatarDataHora(lembrete.dataHora),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { vm.deletarLembrete(lembrete) }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Excluir",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { mostrarDialogo = true },
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Novo Lembrete")
        }
    }

    if (mostrarDialogo) {
        DialogoLembrete(
            aoFechar = { mostrarDialogo = false },
            aoSalvar = {
                vm.salvarLembrete(it)
                mostrarDialogo = false
            }
        )
    }
}

@Composable
private fun DialogoLembrete(
    aoFechar: () -> Unit,
    aoSalvar: (Reminder) -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var dataMs by remember { mutableStateOf<Long?>(null) }
    var hora by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var mostrarData by remember { mutableStateOf(false) }
    var mostrarHora by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = aoFechar,
        title = { Text("Novo lembrete de estudo") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título do lembrete *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedButton(onClick = { mostrarData = true }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.CalendarMonth, null)
                    Spacer(Modifier.width(8.dp))
                    Text(dataMs?.let(::formatarData) ?: "Escolher data")
                }
                OutlinedButton(onClick = { mostrarHora = true }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Schedule, null)
                    Spacer(Modifier.width(8.dp))
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
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    aoSalvar(Reminder(disciplinaId = 0L, titulo = titulo.trim(), dataHora = cal.timeInMillis))
                }
            ) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = aoFechar) { Text("Cancelar") } }
    )

    if (mostrarData) SeletorData({ dataMs = it }, { mostrarData = false })
    if (mostrarHora) SeletorHora({ h, m -> hora = h to m }, { mostrarHora = false })
}
