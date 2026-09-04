package com.example.studyflow.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReceptorLembrete : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra(CHAVE_ID, 0)
        val titulo = intent.getStringExtra(CHAVE_TITULO) ?: "Lembrete de Estudo"
        val texto = intent.getStringExtra(CHAVE_TEXTO) ?: "Hora de revisar sua disciplina!"

        // Exibe a notificação local para o usuário
        NotificationHelper.notificar(context, id, titulo, texto)
    }

    companion object {
        const val ACTION_DISPARAR_LEMBRETE = "com.example.studyflow.ACTION_DISPARAR_LEMBRETE"
        const val CHAVE_ID = "lembrete_id"
        const val CHAVE_TITULO = "lembrete_titulo"
        const val CHAVE_TEXTO = "lembrete_texto"
    }
}
