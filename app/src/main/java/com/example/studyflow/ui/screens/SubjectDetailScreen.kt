package com.example.studyflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.studyflow.ui.screens.tabs.*
import com.example.studyflow.ui.viewmodel.SubjectViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectDetailScreen(vm: SubjectViewModel, idDisciplina: Long, aoVoltar: () -> Unit) {
    val disciplina by vm.disciplina(idDisciplina).collectAsState(initial = null)
    val abas = listOf("Anotações", "Faltas", "Lembretes")
    val pagerState = rememberPagerState(pageCount = { abas.size })
    val escopo = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(disciplina?.nome ?: "Disciplina") },
                navigationIcon = {
                    IconButton(onClick = aoVoltar) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar") }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = pagerState.currentPage) {
                abas.forEachIndexed { i, titulo ->
                    Tab(
                        selected = pagerState.currentPage == i,
                        onClick = { escopo.launch { pagerState.animateScrollToPage(i) } },
                        text = { Text(titulo) },
                        icon = {
                            Icon(
                                when (i) {
                                    0 -> Icons.Default.Notes
                                    1 -> Icons.Default.EventBusy
                                    else -> Icons.Default.Notifications
                                }, null
                            )
                        }
                    )
                }
            }
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { pagina ->
                when (pagina) {
                    0 -> NotesTab(vm, idDisciplina)
                    1 -> AbsencesTab(vm, idDisciplina, disciplina?.maxFaltas ?: 10)
                    2 -> RemindersTab(vm, idDisciplina)
                }
            }
        }
    }
}
