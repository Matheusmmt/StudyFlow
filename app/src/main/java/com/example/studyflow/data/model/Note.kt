package com.example.studyflow.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "anotacoes",
    foreignKeys = [ForeignKey(
        entity = Subject::class,
        parentColumns = ["id"],
        childColumns = ["disciplinaId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [androidx.room.Index("disciplinaId")]
)
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val disciplinaId: Long,
    val titulo: String,
    val conteudo: String,
    val arquivoUri: String? = null, // caminho do PDF/arquivo anexado
    val criadoEm: Long = System.currentTimeMillis()
)
