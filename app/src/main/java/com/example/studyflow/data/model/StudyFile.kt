package com.example.studyflow.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_files")
data class StudyFile(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val subjectId: Int,
    val name: String,
    val uri: String,
    val createdAt: Long = System.currentTimeMillis()
)