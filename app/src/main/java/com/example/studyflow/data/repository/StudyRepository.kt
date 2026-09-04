package com.example.studyflow.data.repository

import android.content.Context
import com.example.studyflow.data.local.AppDatabase
import com.example.studyflow.data.local.PerfilDataStore
import com.example.studyflow.data.model.*
import com.example.studyflow.data.remote.ApiClient
import com.example.studyflow.data.remote.FrasePost
import kotlinx.coroutines.flow.Flow

class StudyRepository(context: Context) {

    private val db = AppDatabase.obter(context)
    private val subjectDao = db.subjectDao()
    private val noteDao = db.noteDao()
    private val absenceDao = db.absenceDao()
    private val reminderDao = db.reminderDao()
    private val perfilDataStore = PerfilDataStore(context)
    private val api = ApiClient.service

    // ---------- Preferences DataStore (Perfil) ----------
    val nomeFlow: Flow<String> = perfilDataStore.nomeFlow
    val matriculaFlow: Flow<String> = perfilDataStore.matriculaFlow
    val semestreAtualFlow: Flow<String> = perfilDataStore.semestreAtualFlow

    suspend fun salvarPerfil(nome: String, matricula: String, semestre: String) {
        perfilDataStore.salvarPerfil(nome, matricula, semestre)
    }

    // ---------- Room: disciplinas ----------
    fun disciplinas(): Flow<List<Subject>> = subjectDao.observarTodas()
    fun disciplinasPorSemestre(semestre: String): Flow<List<Subject>> = subjectDao.observarPorSemestre(semestre)
    fun contarDisciplinasPorSemestre(semestre: String): Flow<Int> = subjectDao.contarPorSemestre(semestre)
    fun disciplina(id: Long): Flow<Subject?> = subjectDao.observarPorId(id)

    suspend fun salvarDisciplina(s: Subject): Long =
        if (s.id == 0L) subjectDao.inserir(s) else { subjectDao.atualizar(s); s.id }
    suspend fun deletarDisciplina(s: Subject) = subjectDao.deletar(s)

    // ---------- Room: anotações e arquivos ----------
    fun anotacoes(disciplinaId: Long): Flow<List<Note>> = noteDao.observarPorDisciplina(disciplinaId)
    suspend fun salvarAnotacao(n: Note) = if (n.id == 0L) noteDao.inserir(n) else { noteDao.atualizar(n); n.id }
    suspend fun deletarAnotacao(n: Note) = noteDao.deletar(n)

    // ---------- Room: faltas ----------
    fun faltas(disciplinaId: Long): Flow<List<Absence>> = absenceDao.observarPorDisciplina(disciplinaId)
    suspend fun registrarFalta(a: Absence) = absenceDao.inserir(a)
    suspend fun deletarFalta(a: Absence) = absenceDao.deletar(a)

    // ---------- Room: lembretes ----------
    fun lembretes(disciplinaId: Long): Flow<List<Reminder>> = reminderDao.observarPorDisciplina(disciplinaId)
    fun proximoLembrete(): Flow<Reminder?> = reminderDao.proximoLembrete(System.currentTimeMillis())
    suspend fun salvarLembrete(r: Reminder): Long =
        if (r.id == 0L) reminderDao.inserir(r) else { reminderDao.atualizar(r); r.id }
    suspend fun deletarLembrete(r: Reminder) = reminderDao.deletar(r)

    // ---------- Retrofit: Frases Motivacionais ----------
    suspend fun obterFrasePorId(id: Int): FrasePost = api.obterFrasePorId(id)
    suspend fun enviarSugestaoFrase(frase: FrasePost): FrasePost = api.enviarSugestaoFrase(frase)
}
