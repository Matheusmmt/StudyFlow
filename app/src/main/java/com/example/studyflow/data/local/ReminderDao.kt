package com.example.studyflow.data.local

import androidx.room.*
import com.example.studyflow.data.model.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM lembretes WHERE disciplinaId = :idDisciplina ORDER BY dataHora ASC")
    fun observarPorDisciplina(idDisciplina: Long): Flow<List<Reminder>>

    @Query("SELECT * FROM lembretes WHERE disciplinaId = :idDisciplina ORDER BY dataHora ASC")
    fun listarPorDisciplina(idDisciplina: Long): Flow<List<Reminder>>

    @Query("SELECT * FROM lembretes WHERE dataHora >= :agora AND concluido = 0 ORDER BY dataHora ASC LIMIT 1")
    fun proximoLembrete(agora: Long): Flow<Reminder?>

    @Insert suspend fun inserir(lembrete: Reminder): Long
    @Update suspend fun atualizar(lembrete: Reminder)
    @Delete suspend fun deletar(lembrete: Reminder)
}
