package com.example.studyflow.data.local

import androidx.room.*
import com.example.studyflow.data.model.Absence
import kotlinx.coroutines.flow.Flow

@Dao
interface AbsenceDao {
    @Query("SELECT * FROM faltas WHERE disciplinaId = :idDisciplina ORDER BY data DESC")
    fun listarPorDisciplina(idDisciplina: Long): Flow<List<Absence>>

    @Query("SELECT COUNT(*) FROM faltas WHERE disciplinaId = :idDisciplina")
    fun contarPorDisciplina(idDisciplina: Long): Flow<Int>

    @Insert suspend fun inserir(falta: Absence): Long
    @Delete suspend fun deletar(falta: Absence)
}
