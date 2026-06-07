package com.example.studyflow.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "disciplinas")
data class Subject(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val professor: String = "",
    val maxFaltas: Int = 10,
    val corHex: String = "#6750A4" // cor de destaque da disciplina
)
