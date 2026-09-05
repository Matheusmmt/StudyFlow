package com.example.studyflow

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.studyflow.ui.screens.AddEditSubjectScreen
import com.example.studyflow.ui.screens.SubjectDetailScreen
import com.example.studyflow.ui.screens.SubjectListScreen
import com.example.studyflow.ui.screens.TelaPerfil
import com.example.studyflow.ui.theme.StudyFlowTheme
import com.example.studyflow.ui.viewmodel.PerfilViewModel
import com.example.studyflow.ui.viewmodel.SubjectDetailViewModel
import com.example.studyflow.ui.viewmodel.SubjectListViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            StudyFlowTheme {
                // Solicita a permissão de notificação
                SolicitarPermissaoNotificacao()

                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "lista"
                ) {
                    // 1. Tela principal (Lista de Disciplinas filtrada pelo semestre do DataStore)
                    composable("lista") {
                        val vmList: SubjectListViewModel = viewModel()
                        SubjectListScreen(
                            vm = vmList,
                            aoAbrirDisciplina = { id -> navController.navigate("detalhe/$id") },
                            aoCriarDisciplina = { navController.navigate("form/0") },
                            aoEditarDisciplina = { id -> navController.navigate("form/$id") },
                            aoAbrirPerfil = { navController.navigate("perfil") }
                        )
                    }

                    // 2. Tela de Detalhes da Disciplina (Anotações, Faltas, Lembretes)
                    composable(
                        route = "detalhe/{id}",
                        arguments = listOf(navArgument("id") { type = NavType.LongType })
                    ) { backStack ->
                        val id = backStack.arguments?.getLong("id") ?: 0L
                        val vmDetail: SubjectDetailViewModel = viewModel(
                            factory = SubjectDetailViewModel.Factory(application, id)
                        )
                        SubjectDetailScreen(
                            vm = vmDetail,
                            idDisciplina = id,
                            aoEditarDisciplina = { idSubject -> navController.navigate("form/$idSubject") },
                            aoVoltar = { navController.popBackStack() }
                        )
                    }

                    // 3. Tela de Criação e Edição de Disciplina
                    composable(
                        route = "form/{id}",
                        arguments = listOf(navArgument("id") { type = NavType.LongType })
                    ) { backStack ->
                        val id = backStack.arguments?.getLong("id") ?: 0L
                        val vmList: SubjectListViewModel = viewModel()
                        val disciplinaFlow = remember(id, vmList) {
                            if (id != 0L) vmList.disciplina(id) else kotlinx.coroutines.flow.flowOf(null)
                        }
                        val disciplinaState by disciplinaFlow.collectAsStateWithLifecycle(initialValue = null)

                        AddEditSubjectScreen(
                            disciplina = disciplinaState,
                            aoSalvar = { disciplinaSalva ->
                                vmList.salvar(disciplinaSalva)
                                navController.popBackStack()
                            },
                            aoVoltar = { navController.popBackStack() }
                        )
                    }

                    // 4. Tela de Perfil do Estudante (DataStore + Retrofit + Semestre)
                    composable("perfil") {
                        val vmPerfil: PerfilViewModel = viewModel()
                        TelaPerfil(
                            vm = vmPerfil,
                            aoVoltar = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Componente Composable que verifica e solicita a permissão de notificação
 * (POST_NOTIFICATIONS) em tempo de execução
 */
@Composable
fun SolicitarPermissaoNotificacao() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val context = LocalContext.current
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { _ -> }

        LaunchedEffect(Unit) {
            val temPermissao = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!temPermissao) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
