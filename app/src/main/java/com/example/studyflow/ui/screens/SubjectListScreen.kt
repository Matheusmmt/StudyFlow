package com.example.studyflow.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studyflow.data.model.Reminder
import com.example.studyflow.data.model.Subject
import com.example.studyflow.ui.viewmodel.SubjectListViewModel
import com.example.studyflow.utils.calcularProgressoAutomatico

/**
 * Tela Principal de Lista de Disciplinas com suporte a Tema Claro e Escuro automático do sistema.
 */
@Composable
fun SubjectListScreen(
    vm: SubjectListViewModel,
    aoAbrirDisciplina: (Long) -> Unit,
    aoCriarDisciplina: () -> Unit,
    aoEditarDisciplina: (Long) -> Unit,
    aoAbrirPerfil: () -> Unit
) {
    val disciplinas by vm.disciplinas.collectAsStateWithLifecycle()
    val proximoLembrete by vm.proximoLembrete.collectAsStateWithLifecycle()
    val busca by vm.busca.collectAsStateWithLifecycle()

    var mostrandoBusca by remember { mutableStateOf(false) }

    // 1. Fundo da Tela dinâmico do tema (Light/Dark)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 28.dp)
        ) {
            // 2. Header (Topo)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Lado esquerdo (Column com saudação)
                Column {
                    Text(
                        text = "Olá, Aluno! 👋",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Organize seus estudos",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }

                // Lado direito: Ícones de Busca e Perfil do Usuário
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { mostrandoBusca = !mostrandoBusca }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = aoAbrirPerfil) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Perfil",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            // Barra de busca opcional
            AnimatedVisibility(visible = mostrandoBusca) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = busca,
                    onValueChange = { vm.busca.value = it },
                    placeholder = { Text("Buscar disciplina...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                    trailingIcon = {
                        if (busca.isNotBlank()) {
                            IconButton(onClick = { vm.busca.value = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = CircleShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            // Destaque de próximo lembrete se existir
            proximoLembrete?.let { lembrete ->
                Spacer(modifier = Modifier.height(16.dp))
                CardProximoLembrete(lembrete = lembrete)
            }

            // 3. Sub-Header (Seção de Disciplinas)
            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Minhas Disciplinas",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Lista de Cards
            if (disciplinas.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "Nenhuma disciplina cadastrada",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Toque no botão + para adicionar uma disciplina.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(disciplinas, key = { it.id }) { disciplina ->
                        SubjectCard(
                            disciplina = disciplina,
                            aoClicar = { aoAbrirDisciplina(disciplina.id) },
                            aoEditar = { aoEditarDisciplina(disciplina.id) },
                            aoExcluir = { vm.deletar(disciplina) }
                        )
                    }
                }
            }
        }

        // Botão flutuante (+) de Adicionar Disciplina na parte inferior direita
        FloatingActionButton(
            onClick = aoCriarDisciplina,
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 20.dp, end = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Criar Disciplina",
                tint = Color.White
            )
        }
    }
}

/**
 * Composable do Card da Disciplina adaptado aos temas Claro e Escuro.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SubjectCard(
    disciplina: Subject,
    aoClicar: () -> Unit,
    aoEditar: () -> Unit,
    aoExcluir: () -> Unit
) {
    var menuExpandido by remember { mutableStateOf(false) }

    val progresso = remember(disciplina.totalAulas, disciplina.diasSemana, disciplina.dataInicio) {
        calcularProgressoAutomatico(
            totalAulas = disciplina.totalAulas,
            diasSemanaStr = disciplina.diasSemana,
            dataInicioMs = disciplina.dataInicio
        )
    }

    val corFundo = remember(disciplina.cor, disciplina.corHex) {
        try {
            if (disciplina.cor != 0L) Color(disciplina.cor)
            else Color(disciplina.corHex.toColorInt())
        } catch (_: Exception) {
            Color(0xFF7C4DFF)
        }
    }

    val iconeVector: ImageVector = remember(disciplina.icone) {
        when (disciplina.icone.lowercase()) {
            "code", "</>" -> Icons.Default.Code
            "science", "quimica" -> Icons.Default.Science
            "history", "historia" -> Icons.Default.Book
            "psychology", "psicologia" -> Icons.Default.Psychology
            "calculate", "calculo", "math" -> Icons.Default.Functions
            else -> Icons.Default.School
        }
    }

    val diasEHorario = remember(disciplina.diasSemana, disciplina.horario) {
        val dias = when (disciplina.diasSemana.uppercase()) {
            "SEG", "SEG,QUA" -> "Segunda"
            "TER" -> "Terça"
            "QUA" -> "Quarta"
            "QUI" -> "Quinta"
            "SEX" -> "Sexta"
            "SAB" -> "Sábado"
            "DOM" -> "Domingo"
            else -> disciplina.diasSemana.ifBlank { "Segunda" }
        }
        val hor = disciplina.horario.orEmpty()
        if (hor.isNotBlank()) {
            if (hor.contains("•") || hor.contains(dias, ignoreCase = true)) hor else "$dias • $hor"
        } else {
            "$dias • 08:00 - 09:40"
        }
    }

    val porcentagemProgresso = (progresso * 100).toInt()

    // Card Principal adaptado ao tema
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(
                onClick = aoClicar,
                onLongClick = { menuExpandido = true }
            )
    ) {
        // Row interna do Card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bloco do Ícone (Esquerda)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(corFundo),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconeVector,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Textos (Centro)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                // Linha 1: Nome da disciplina em Negrito, 16.sp
                Text(
                    text = disciplina.nome,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                // Linha 2: Dias da semana e horário em 12.sp
                Text(
                    text = diasEHorario,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )

                // Linha 3: Progresso na cor do tema, 12.sp
                Text(
                    text = "$porcentagemProgresso% do conteúdo estudado",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp
                )
            }

            // Ações (Direita)
            Box {
                IconButton(onClick = { menuExpandido = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Opções",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                DropdownMenu(
                    expanded = menuExpandido,
                    onDismissRequest = { menuExpandido = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Editar disciplina") },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                        onClick = {
                            menuExpandido = false
                            aoEditar()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Excluir disciplina") },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
                        onClick = {
                            menuExpandido = false
                            aoExcluir()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CardProximoLembrete(lembrete: Reminder) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.Notifications,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Próximo lembrete de estudo",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = lembrete.titulo,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
