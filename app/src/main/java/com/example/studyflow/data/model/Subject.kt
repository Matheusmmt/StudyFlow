package com.example.studyflow.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "disciplinas")
data class Subject(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val professor: String? = null,
    val horario: String? = null,
    val maxFaltas: Int = 15,
    val totalAulas: Int = 30,
    val diasSemana: String = "SEG,QUA",
    val dataInicio: Long = System.currentTimeMillis(),
    val cor: Long = 0xFF8B5CF6,
    val icone: String = "book",
    val semestre: String = "2026.1",
    val corHex: String = "#8B5CF6",
    val createdAt: Long = System.currentTimeMillis()
)
