package com.example.studyflow.data.repository

import com.example.studyflow.data.local.*
import com.example.studyflow.data.model.*

class SubjectRepository(
    private val subjectDao: SubjectDao,
    private val noteDao: NoteDao,
    private val absenceDao: AbsenceDao,
    private val reminderDao: ReminderDao
) {
    // Disciplinas
    val disciplinas = subjectDao.listarTodas()
    fun disciplina(id: Long) = subjectDao.buscarPorId(id)
    suspend fun salvar(d: Subject) = if (d.id == 0L) subjectDao.inserir(d) else { subjectDao.atualizar(d); d.id }
    suspend fun excluir(d: Subject) = subjectDao.deletar(d)

    // Anotações
    fun notas(idDisciplina: Long) = noteDao.listarPorDisciplina(idDisciplina)
    suspend fun salvarNota(n: Note) = if (n.id == 0L) noteDao.inserir(n) else { noteDao.atualizar(n); n.id }
    suspend fun excluirNota(n: Note) = noteDao.deletar(n)

    // Faltas
    fun faltas(idDisciplina: Long) = absenceDao.listarPorDisciplina(idDisciplina)
    fun totalFaltas(idDisciplina: Long) = absenceDao.contarPorDisciplina(idDisciplina)
    suspend fun salvarFalta(f: Absence) = absenceDao.inserir(f)
    suspend fun excluirFalta(f: Absence) = absenceDao.deletar(f)

    // Lembretes
    fun lembretes(idDisciplina: Long) = reminderDao.listarPorDisciplina(idDisciplina)
    suspend fun salvarLembrete(r: Reminder) = if (r.id == 0L) reminderDao.inserir(r) else { reminderDao.atualizar(r); r.id }
    suspend fun excluirLembrete(r: Reminder) = reminderDao.deletar(r)
}
