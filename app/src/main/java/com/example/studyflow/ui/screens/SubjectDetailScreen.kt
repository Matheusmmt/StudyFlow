package com.example.studyflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studyflow.ui.screens.tabs.*
import com.example.studyflow.ui.viewmodel.SubjectDetailViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectDetailScreen(
    vm: SubjectDetailViewModel,
    idDisciplina: Long,
    aoEditarDisciplina: (Long) -> Unit,
    aoVoltar: () -> Unit
) {
    val disciplina by vm.disciplina.collectAsStateWithLifecycle()
    val abas = remember { listOf("Anotações", "Arquivos", "Faltas", "Lembretes") }
    val pagerState = rememberPagerState(pageCount = { abas.size })
    val escopo = rememberCoroutineScope()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = disciplina?.nome ?: "Detalhes da Disciplina",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = aoVoltar) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { aoEditarDisciplina(idDisciplina) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar disciplina",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
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
                .fillMaxSize()
                .padding(padding)
        ) {
            // Exibe professor / horário se informados, sem duplicar o nome e o ícone grande da disciplina
            disciplina?.let { sub ->
                val prof = sub.professor
                val hor = sub.horario
                val temProf = !prof.isNullOrBlank()
                val temHor = !hor.isNullOrBlank()

                if (temProf || temHor) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 0.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            if (!prof.isNullOrBlank()) {
                                Text(
                                    text = "Professor: $prof",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (!hor.isNullOrBlank()) {
                                Text(
                                    text = hor,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                }
            }

            // Navegação por abas (TabRow)
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
                indicator = { tabPositions ->
                    if (pagerState.currentPage < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                divider = {}
            ) {
                abas.forEachIndexed { index, titulo ->
                    val selecionado = pagerState.currentPage == index
                    Tab(
                        selected = selecionado,
                        onClick = { escopo.launch { pagerState.animateScrollToPage(index) } },
                        text = {
                            Text(
                                text = titulo,
                                fontWeight = if (selecionado) FontWeight.Bold else FontWeight.Normal,
                                color = if (selecionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { pagina ->
                when (pagina) {
                    0 -> NotesTab(vm = vm)
                    1 -> ArquivosTab(vm = vm, idDisciplina = idDisciplina)
                    2 -> AbsencesTab(vm = vm, maxFaltas = disciplina?.maxFaltas ?: 15)
                    3 -> RemindersTab(vm = vm)
                }
            }
        }
    }
}

/** Ícone estilizado nas telas do app */
@Composable
fun IconeDisciplina(cor: Long, icone: String, tamanho: Int = 48) {
    val vetor: ImageVector = when (icone.lowercase()) {
        "code", "</>" -> Icons.Default.Code
        "science", "quimica" -> Icons.Default.Science
        "history", "historia" -> Icons.Default.Book
        "psychology", "psicologia" -> Icons.Default.Psychology
        "calculate", "calculo", "math" -> Icons.Default.Functions
        else -> Icons.Default.School
    }
    Box(
        modifier = Modifier
            .size(tamanho.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(cor)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = vetor,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size((tamanho * 0.52).dp)
        )
    }
}
