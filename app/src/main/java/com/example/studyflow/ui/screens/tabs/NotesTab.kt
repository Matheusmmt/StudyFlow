package com.example.studyflow.ui.screens.tabs

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studyflow.data.model.Note
import com.example.studyflow.ui.components.formatarData
import com.example.studyflow.ui.viewmodel.SubjectDetailViewModel

@Composable
fun NotesTab(vm: SubjectDetailViewModel) {
    val notas by vm.anotacoes.collectAsStateWithLifecycle()
    var mostrarDialogo by remember { mutableStateOf(false) }
    var editando by remember { mutableStateOf<Note?>(null) }

    Box(Modifier.fillMaxSize()) {
        if (notas.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "Nenhuma anotação ainda. Toque no botão + para criar.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notas, key = { it.id }) { note ->
                    ItemAnotacaoEstilizado(
                        nota = note,
                        aoEditar = { editando = note; mostrarDialogo = true },
                        aoExcluir = { vm.deletarAnotacao(note) }
                    )
                }
            }
        }
        FloatingActionButton(
            onClick = { editando = null; mostrarDialogo = true },
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Nova Anotação")
        }
    }

    if (mostrarDialogo) {
        DialogoNota(
            nota = editando,
            aoFechar = { mostrarDialogo = false },
            aoSalvar = {
                vm.salvarAnotacao(it)
                mostrarDialogo = false
            }
        )
    }
}

@Composable
private fun ItemAnotacaoEstilizado(
    nota: Note,
    aoEditar: () -> Unit,
    aoExcluir: () -> Unit
) {
    var menuExpandido by remember { mutableStateOf(false) }

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
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF3F51B5)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Description,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = nota.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = formatarData(nota.criadoEm),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box {
                IconButton(onClick = { menuExpandido = true }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Opções",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                DropdownMenu(
                    expanded = menuExpandido,
                    onDismissRequest = { menuExpandido = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Editar") },
                        leadingIcon = { Icon(Icons.Default.Edit, null) },
                        onClick = { menuExpandido = false; aoEditar() }
                    )
                    DropdownMenuItem(
                        text = { Text("Excluir") },
                        leadingIcon = { Icon(Icons.Default.Delete, null) },
                        onClick = { menuExpandido = false; aoExcluir() }
                    )
                }
            }
        }
    }
}

@Composable
private fun DialogoNota(
    nota: Note?,
    aoFechar: () -> Unit,
    aoSalvar: (Note) -> Unit
) {
    var titulo by remember { mutableStateOf(nota?.titulo ?: "") }
    var conteudo by remember { mutableStateOf(nota?.conteudo ?: "") }

    AlertDialog(
        onDismissRequest = aoFechar,
        title = { Text(if (nota == null) "Nova anotação" else "Editar anotação") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = conteudo,
                    onValueChange = { conteudo = it },
                    label = { Text("Conteúdo") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = titulo.isNotBlank(),
                onClick = {
                    aoSalvar(
                        (nota ?: Note(disciplinaId = 0L, titulo = "", conteudo = "")).copy(
                            titulo = titulo.trim(),
                            conteudo = conteudo.trim()
                        )
                    )
                }
            ) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = aoFechar) { Text("Cancelar") } }
    )
}
