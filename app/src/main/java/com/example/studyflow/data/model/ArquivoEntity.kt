package com.example.studyflow.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Entidade Room para armazenamento de arquivos/anexos acadêmicos por disciplina.
 */
@Entity(
    tableName = "arquivos",
    foreignKeys = [ForeignKey(
        entity = Subject::class,
        parentColumns = ["id"],
        childColumns = ["disciplinaId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [androidx.room.Index("disciplinaId")]
)
data class ArquivoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val disciplinaId: Long,
    val nomeArquivo: String,
    val uri: String,
    val tamanhoOuTipo: String? = null,
    val dataAdicao: Long = System.currentTimeMillis()
)
