package com.example.studyflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.studyflow.data.model.Subject
import com.example.studyflow.ui.viewmodel.SubjectViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectListScreen(vm: SubjectViewModel, aoAbrir: (Long) -> Unit) {
    val disciplinas by vm.disciplinas.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }
    var editando by remember { mutableStateOf<Subject?>(null) }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text("StudyFlow", fontWeight = FontWeight.Bold)
                        Text("Suas disciplinas", style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { editando = null; mostrarDialogo = true },
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("Nova disciplina") }
            )
        }
    ) { padding ->
        if (disciplinas.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                Text("Nenhuma disciplina ainda.\nToque em + para criar.",
                    style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(disciplinas, key = { it.id }) { d ->
                    CardDisciplina(d,
                        aoClicar = { aoAbrir(d.id) },
                        aoEditar = { editando = d; mostrarDialogo = true },
                        aoExcluir = { vm.excluirDisciplina(d) }
                    )
                }
            }
        }
    }

    if (mostrarDialogo) {
        DialogoDisciplina(
            disciplina = editando,
            aoFechar = { mostrarDialogo = false },
            aoSalvar = { vm.salvarDisciplina(it); mostrarDialogo = false }
        )
    }
}

@Composable
private fun CardDisciplina(d: Subject, aoClicar: () -> Unit, aoEditar: () -> Unit, aoExcluir: () -> Unit) {
    val cor = runCatching { Color(android.graphics.Color.parseColor(d.corHex)) }.getOrDefault(MaterialTheme.colorScheme.primary)
    ElevatedCard(onClick = aoClicar, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(cor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center) {
                Text(d.nome.take(1).uppercase(),
                    style = MaterialTheme.typography.titleLarge, color = cor, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(d.nome, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                if (d.professor.isNotBlank())
                    Text(d.professor, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = aoEditar) { Icon(Icons.Default.Edit, "Editar") }
            IconButton(onClick = aoExcluir) { Icon(Icons.Default.Delete, "Excluir") }
        }
    }
}

@Composable
private fun DialogoDisciplina(disciplina: Subject?, aoFechar: () -> Unit, aoSalvar: (Subject) -> Unit) {
    var nome by remember { mutableStateOf(disciplina?.nome ?: "") }
    var prof by remember { mutableStateOf(disciplina?.professor ?: "") }
    var maxF by remember { mutableStateOf((disciplina?.maxFaltas ?: 10).toString()) }

    AlertDialog(
        onDismissRequest = aoFechar,
        title = { Text(if (disciplina == null) "Nova disciplina" else "Editar disciplina") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(nome, { nome = it }, label = { Text("Nome") }, singleLine = true)
                OutlinedTextField(prof, { prof = it }, label = { Text("Professor") }, singleLine = true)
                OutlinedTextField(maxF, { maxF = it.filter(Char::isDigit) },
                    label = { Text("Máx. de faltas permitidas") }, singleLine = true)
            }
        },
        confirmButton = {
            TextButton(
                enabled = nome.isNotBlank(),
                onClick = {
                    aoSalvar(
                        (disciplina ?: Subject(nome = "")).copy(
                            nome = nome.trim(),
                            professor = prof.trim(),
                            maxFaltas = maxF.toIntOrNull() ?: 10
                        )
                    )
                }
            ) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = aoFechar) { Text("Cancelar") } }
    )
}
