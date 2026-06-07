package com.example.studyflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.studyflow.data.local.AppDatabase
import com.example.studyflow.data.repository.SubjectRepository
import com.example.studyflow.ui.screens.SubjectDetailScreen
import com.example.studyflow.ui.screens.SubjectListScreen
import com.example.studyflow.ui.theme.StudyFlowTheme
import com.example.studyflow.ui.viewmodel.SubjectViewModel
import com.example.studyflow.ui.viewmodel.SubjectViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.obterInstancia(applicationContext)
        val repo = SubjectRepository(db.subjectDao(), db.noteDao(), db.absenceDao(), db.reminderDao())

        setContent {
            StudyFlowTheme {
                val nav = rememberNavController()
                val vm: SubjectViewModel = viewModel(factory = SubjectViewModelFactory(repo))

                NavHost(navController = nav, startDestination = "lista") {
                    composable("lista") {
                        SubjectListScreen(vm) { id -> nav.navigate("detalhe/$id") }
                    }
                    composable(
                        route = "detalhe/{id}",
                        arguments = listOf(navArgument("id") { type = NavType.LongType })
                    ) { backStack ->
                        val id = backStack.arguments?.getLong("id") ?: 0L
                        SubjectDetailScreen(vm, id) { nav.popBackStack() }
                    }
                }
            }
        }
    }
}
