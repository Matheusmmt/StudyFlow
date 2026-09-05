package com.example.studyflow.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.studyflow.data.model.*

@Database(
    entities = [Subject::class, Note::class, Absence::class, Reminder::class, ArquivoEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao
    abstract fun noteDao(): NoteDao
    abstract fun absenceDao(): AbsenceDao
    abstract fun reminderDao(): ReminderDao
    abstract fun arquivoDao(): ArquivoDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun obterInstancia(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "studyflow.db"
                )
                .fallbackToDestructiveMigration(true)
                .build().also { INSTANCE = it }
            }

        fun obter(context: Context): AppDatabase = obterInstancia(context)
    }
}
