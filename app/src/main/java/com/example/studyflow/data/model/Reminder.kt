package com.example.studyflow.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "lembretes",
    foreignKeys = [ForeignKey(
        entity = Subject::class,
        parentColumns = ["id"],
        childColumns = ["disciplinaId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [androidx.room.Index("disciplinaId")]
)
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val disciplinaId: Long,
    val titulo: String,
    val dataHora: Long, // timestamp completo
    val concluido: Boolean = false
)
