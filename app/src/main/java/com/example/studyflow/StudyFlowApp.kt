package com.example.studyflow

import android.app.Application
import com.example.studyflow.utils.NotificationHelper

class StudyFlowApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.criarCanais(this)
    }
}