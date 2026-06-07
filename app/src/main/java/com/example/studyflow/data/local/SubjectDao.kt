package com.example.studyflow.data.local

import androidx.room.*
import com.example.studyflow.data.model.Subject
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {
    @Query("SELECT * FROM disciplinas ORDER BY nome ASC")
    fun listarTodas(): Flow<List<Subject>>

    @Query("SELECT * FROM disciplinas WHERE id = :id")
    fun buscarPorId(id: Long): Flow<Subject?>

    @Insert suspend fun inserir(disciplina: Subject): Long
    @Update suspend fun atualizar(disciplina: Subject)
    @Delete suspend fun deletar(disciplina: Subject)
}
