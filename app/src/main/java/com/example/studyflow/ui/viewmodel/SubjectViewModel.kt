package com.example.studyflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyflow.data.model.*
import com.example.studyflow.data.repository.SubjectRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SubjectViewModel(private val repo: SubjectRepository) : ViewModel() {

    val disciplinas: StateFlow<List<Subject>> =
        repo.disciplinas.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun disciplina(id: Long) = repo.disciplina(id)
    fun notas(id: Long) = repo.notas(id)
    fun faltas(id: Long) = repo.faltas(id)
    fun totalFaltas(id: Long) = repo.totalFaltas(id)
    fun lembretes(id: Long) = repo.lembretes(id)

    fun salvarDisciplina(d: Subject) = viewModelScope.launch { repo.salvar(d) }
    fun excluirDisciplina(d: Subject) = viewModelScope.launch { repo.excluir(d) }

    fun salvarNota(n: Note) = viewModelScope.launch { repo.salvarNota(n) }
    fun excluirNota(n: Note) = viewModelScope.launch { repo.excluirNota(n) }

    fun adicionarFalta(idDisciplina: Long, data: Long, motivo: String = "") =
        viewModelScope.launch { repo.salvarFalta(Absence(disciplinaId = idDisciplina, data = data, motivo = motivo)) }
    fun excluirFalta(f: Absence) = viewModelScope.launch { repo.excluirFalta(f) }

    fun salvarLembrete(r: Reminder) = viewModelScope.launch { repo.salvarLembrete(r) }
    fun excluirLembrete(r: Reminder) = viewModelScope.launch { repo.excluirLembrete(r) }
}
