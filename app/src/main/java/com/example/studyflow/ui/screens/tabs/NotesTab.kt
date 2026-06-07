package com.example.studyflow.ui.screens.tabs

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.studyflow.data.model.Note
import com.example.studyflow.ui.viewmodel.SubjectViewModel

@Composable
fun NotesTab(vm: SubjectViewModel, idDisciplina: Long) {
    val notas by vm.notas(idDisciplina).collectAsState(initial = emptyList())
    var mostrarDialogo by remember { mutableStateOf(false) }
    var editando by remember { mutableStateOf<Note?>(null) }

    Box(Modifier.fillMaxSize()) {
        if (notas.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Sem anotações ainda")
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notas, key = { it.id }) { n ->
                    ElevatedCard {
                        Row(Modifier.padding(16.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(n.titulo, style = MaterialTheme.typography.titleMedium)
                                Text(n.conteudo, style = MaterialTheme.typography.bodyMedium, maxLines = 3)
                                if (n.arquivoUri != null)
                                    AssistChip(onClick = {}, label = { Text("Arquivo anexado") },
                                        leadingIcon = { Icon(Icons.Default.AttachFile, null) })
                            }
                            IconButton(onClick = { editando = n; mostrarDialogo = true }) { Icon(Icons.Default.Edit, null) }
                            IconButton(onClick = { vm.excluirNota(n) }) { Icon(Icons.Default.Delete, null) }
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { editando = null; mostrarDialogo = true },
            modifier = Modifier.align(androidx.compose.ui.Alignment.BottomEnd).padding(16.dp)
        ) { Icon(Icons.Default.Add, null) }
    }

    if (mostrarDialogo) {
        DialogoNota(editando, idDisciplina, { mostrarDialogo = false }) {
            vm.salvarNota(it); mostrarDialogo = false
        }
    }
}

@Composable
private fun DialogoNota(nota: Note?, idDisciplina: Long, aoFechar: () -> Unit, aoSalvar: (Note) -> Unit) {
    var titulo by remember { mutableStateOf(nota?.titulo ?: "") }
    var conteudo by remember { mutableStateOf(nota?.conteudo ?: "") }
    var uriArquivo by remember { mutableStateOf(nota?.arquivoUri) }

    // Seletor de arquivos (PDF e outros)
    val seletor = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let { uriArquivo = it.toString() }
    }

    AlertDialog(
        onDismissRequest = aoFechar,
        title = { Text(if (nota == null) "Nova anotação" else "Editar anotação") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(titulo, { titulo = it }, label = { Text("Título") }, singleLine = true)
                OutlinedTextField(conteudo, { conteudo = it }, label = { Text("Conteúdo") }, minLines = 3)
                OutlinedButton(onClick = { seletor.launch(arrayOf("application/pdf", "image/*", "*/*")) }) {
                    Icon(Icons.Default.AttachFile, null); Spacer(Modifier.width(8.dp))
                    Text(if (uriArquivo == null) "Anexar arquivo" else "Arquivo anexado ✓")
                }
            }
        },
        confirmButton = {
            TextButton(enabled = titulo.isNotBlank(), onClick = {
                aoSalvar((nota ?: Note(disciplinaId = idDisciplina, titulo = "", conteudo = ""))
                    .copy(titulo = titulo, conteudo = conteudo, arquivoUri = uriArquivo))
            }) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = aoFechar) { Text("Cancelar") } }
    )
}
