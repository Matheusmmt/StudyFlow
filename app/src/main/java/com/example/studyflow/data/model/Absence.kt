package com.example.studyflow.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "faltas",
    foreignKeys = [ForeignKey(
        entity = Subject::class,
        parentColumns = ["id"],
        childColumns = ["disciplinaId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [androidx.room.Index("disciplinaId")]
)
data class Absence(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val disciplinaId: Long,
    val data: Long,           // timestamp do dia da falta
    val motivo: String = ""
)
