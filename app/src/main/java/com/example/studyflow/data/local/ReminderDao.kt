package com.example.studyflow.data.local

import androidx.room.*
import com.example.studyflow.data.model.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM lembretes WHERE disciplinaId = :idDisciplina ORDER BY dataHora ASC")
    fun listarPorDisciplina(idDisciplina: Long): Flow<List<Reminder>>

    @Insert suspend fun inserir(lembrete: Reminder): Long
    @Update suspend fun atualizar(lembrete: Reminder)
    @Delete suspend fun deletar(lembrete: Reminder)
}
