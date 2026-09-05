package com.example.studyflow.data.local

import androidx.room.*
import com.example.studyflow.data.model.ArquivoEntity
import kotlinx.coroutines.flow.Flow

/**
 * Interface DAO para operações com arquivos/anexos no banco de dados Room.
 */
@Dao
interface ArquivoDao {

    @Query("SELECT * FROM arquivos WHERE disciplinaId = :disciplinaId ORDER BY dataAdicao DESC")
    fun observarPorDisciplina(disciplinaId: Long): Flow<List<ArquivoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(arquivo: ArquivoEntity): Long

    @Delete
    suspend fun deletar(arquivo: ArquivoEntity)
}
