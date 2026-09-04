package com.example.studyflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.studyflow.data.model.Subject
import com.example.studyflow.ui.components.IconeDisciplina
import com.example.studyflow.ui.components.SeletorData
import com.example.studyflow.ui.components.formatarData

private val CORES = listOf(0xFF8B5CF6, 0xFF22C55E, 0xFFF59E0B, 0xFF3B82F6, 0xFFEC4899, 0xFFEF4444)
private val ICONES = listOf("book", "calculate", "science", "code", "history", "psychology")
private val TODOS_DIAS_SEMANA = listOf("SEG", "TER", "QUA", "QUI", "SEX", "SAB", "DOM")

/**
 * Tela de Criação e Edição de Disciplina.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSubjectScreen(
    disciplina: Subject?,
    aoSalvar: (Subject) -> Unit,
    aoVoltar: () -> Unit
) {
    // Inicialização do estado vinculada à chave `disciplina` para preencher os campos na edição
    var nome by remember(disciplina) { mutableStateOf(disciplina?.nome ?: "") }
    var professor by remember(disciplina) { mutableStateOf(disciplina?.professor ?: "") }
    var horario by remember(disciplina) { mutableStateOf(disciplina?.horario ?: "") }
    var maxFaltas by remember(disciplina) { mutableStateOf((disciplina?.maxFaltas ?: 15).toString()) }
    var totalAulas by remember(disciplina) { mutableStateOf((disciplina?.totalAulas ?: 30).toString()) }
    var dataInicioMs by remember(disciplina) { mutableStateOf(disciplina?.dataInicio ?: System.currentTimeMillis()) }
    var diasSelecionados by remember(disciplina) {
        mutableStateOf(
            disciplina?.diasSemana?.split(",")?.map { it.trim().uppercase() }?.filter { it.isNotBlank() }
                ?: listOf("SEG", "QUA")
        )
    }
    var cor by remember(disciplina) { mutableStateOf(disciplina?.cor ?: CORES.first()) }
    var icone by remember(disciplina) { mutableStateOf(disciplina?.icone ?: "book") }

    var mostrarSeletorDataInicio by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(if (disciplina == null) "Nova disciplina" else "Editar disciplina") },
                navigationIcon = {
                    IconButton(onClick = aoVoltar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                value = nome, onValueChange = { nome = it },
                label = { Text("Nome da disciplina *") },
                singleLine = true, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )
            OutlinedTextField(
                value = professor, onValueChange = { professor = it },
                label = { Text("Professor") },
                singleLine = true, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )
            OutlinedTextField(
                value = horario, onValueChange = { horario = it },
                label = { Text("Horário (ex: Segunda • 08:00 - 09:40)") },
                singleLine = true, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )
            OutlinedTextField(
                value = maxFaltas,
                onValueChange = { maxFaltas = it.filter(Char::isDigit) },
                label = { Text("Máximo de faltas permitidas") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            // Configuração para Progresso Automático: Total de Aulas, Data de Início e Dias da Semana
            Text(
                "Configuração do Semestre & Progresso",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = totalAulas,
                onValueChange = { totalAulas = it.filter(Char::isDigit) },
                label = { Text("Total de aulas planejadas no semestre *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            OutlinedButton(
                onClick = { mostrarSeletorDataInicio = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.CalendarMonth, null)
                Spacer(Modifier.width(8.dp))
                Text("Data de Início das Aulas: ${formatarData(dataInicioMs)}")
            }

            Text("Dias da semana com aula:", style = MaterialTheme.typography.bodyMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TODOS_DIAS_SEMANA.forEach { dia ->
                    val selecionado = dia in diasSelecionados
                    FilterChip(
                        selected = selecionado,
                        onClick = {
                            diasSelecionados = if (selecionado) {
                                if (diasSelecionados.size > 1) diasSelecionados - dia else diasSelecionados
                            } else {
                                diasSelecionados + dia
                            }
                        },
                        label = { Text(dia, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Text("Cor")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CORES.forEach { c ->
                    Box(
                        Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(c))
                            .border(
                                width = if (c == cor) 3.dp else 0.dp,
                                color = MaterialTheme.colorScheme.onBackground,
                                shape = CircleShape
                            )
                            .clickable { cor = c }
                    )
                }
            }

            Text("Ícone")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ICONES.forEach { i ->
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .border(
                                width = if (i == icone) 2.dp else 0.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { icone = i }
                            .padding(2.dp)
                    ) { IconeDisciplina(cor, i, tamanho = 40) }
                }
            }

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    val diasStr = diasSelecionados.joinToString(",")
                    aoSalvar(
                        (disciplina ?: Subject(nome = "")).copy(
                            nome = nome.trim(),
                            professor = professor.trim().ifBlank { null },
                            horario = horario.trim().ifBlank { null },
                            maxFaltas = maxFaltas.toIntOrNull() ?: 15,
                            totalAulas = totalAulas.toIntOrNull() ?: 30,
                            diasSemana = diasStr,
                            dataInicio = dataInicioMs,
                            cor = cor,
                            icone = icone
                        )
                    )
                },
                enabled = nome.isNotBlank() && totalAulas.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp)
            ) { Text("Salvar disciplina") }
        }
    }

    if (mostrarSeletorDataInicio) {
        SeletorData(
            aoSelecionar = { dataInicioMs = it },
            aoFechar = { mostrarSeletorDataInicio = false }
        )
    }
}
