package com.example.studyflow.utils

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.studyflow.MainActivity

object NotificationHelper {

    const val CANAL_LEMBRETES = "study_reminders"

    fun criarCanais(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_LEMBRETES,
                "Lembretes de estudo",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Avisos de estudo e revisões das disciplinas" }
            context.getSystemService(NotificationManager::class.java)?.createNotificationChannel(canal)
        }
    }

    fun notificar(context: Context, id: Int, titulo: String, texto: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return

        val intent = Intent(context, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pending = PendingIntent.getActivity(
            context, id, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificacao = NotificationCompat.Builder(context, CANAL_LEMBRETES)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setStyle(NotificationCompat.BigTextStyle().bigText(texto))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pending)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(id, notificacao)
    }

    /**
     * Agenda um alarme exato usando AlarmManager com setExactAndAllowWhileIdle.
     * Se a data/hora for menor ou igual ao momento atual (passado/presente),
     * o alarme NÃO é agendado para evitar o disparo imediato.
     */
    fun agendarLembrete(context: Context, lembreteId: Long, titulo: String, texto: String, quando: Long) {
        val agora = System.currentTimeMillis()

        // Validação: Não agenda se o horário agendado for no passado ou no momento atual
        if (quando <= agora) {
            cancelarLembrete(context, lembreteId)
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val intent = Intent(context, ReceptorLembrete::class.java).apply {
            action = ReceptorLembrete.ACTION_DISPARAR_LEMBRETE
            putExtra(ReceptorLembrete.CHAVE_ID, lembreteId.toInt())
            putExtra(ReceptorLembrete.CHAVE_TITULO, titulo)
            putExtra(ReceptorLembrete.CHAVE_TEXTO, texto)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            lembreteId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                quando,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                quando,
                pendingIntent
            )
        }
    }

    /**
     * Cancela um alarme anteriormente agendado no AlarmManager.
     */
    fun cancelarLembrete(context: Context, lembreteId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, ReceptorLembrete::class.java).apply {
            action = ReceptorLembrete.ACTION_DISPARAR_LEMBRETE
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            lembreteId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
