package com.example.studyflow.data.local

import androidx.room.*
import com.example.studyflow.data.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM anotacoes WHERE disciplinaId = :idDisciplina ORDER BY criadoEm DESC")
    fun listarPorDisciplina(idDisciplina: Long): Flow<List<Note>>

    @Insert suspend fun inserir(nota: Note): Long
    @Update suspend fun atualizar(nota: Note)
    @Delete suspend fun deletar(nota: Note)
}
