package com.example.studyflow.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import java.text.SimpleDateFormat
import java.util.*


fun normalizarDataUtcParaLocal(utcMillis: Long): Long {
    val calUtc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        timeInMillis = utcMillis
    }
    val ano = calUtc[Calendar.YEAR]
    val mes = calUtc[Calendar.MONTH]
    val dia = calUtc[Calendar.DAY_OF_MONTH]

    val calLocal = Calendar.getInstance().apply {
        clear()
        set(ano, mes, dia, 0, 0, 0)
    }
    return calLocal.timeInMillis
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeletorData(aoSelecionar: (Long) -> Unit, aoFechar: () -> Unit) {
    val estado = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = aoFechar,
        confirmButton = {
            TextButton(onClick = {
                estado.selectedDateMillis?.let { utcMs ->
                    aoSelecionar(normalizarDataUtcParaLocal(utcMs))
                }
                aoFechar()
            }) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = aoFechar) { Text("Cancelar") } }
    ) { DatePicker(state = estado) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeletorHora(aoSelecionar: (Int, Int) -> Unit, aoFechar: () -> Unit) {
    val agora = Calendar.getInstance()
    val estado = rememberTimePickerState(
        initialHour = agora[Calendar.HOUR_OF_DAY],
        initialMinute = agora[Calendar.MINUTE],
        is24Hour = true
    )
    AlertDialog(
        onDismissRequest = aoFechar,
        confirmButton = {
            TextButton(onClick = { aoSelecionar(estado.hour, estado.minute); aoFechar() }) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = aoFechar) { Text("Cancelar") } },
        text = { TimePicker(state = estado) }
    )
}

fun formatarData(ts: Long): String =
    SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR")).format(Date(ts))

fun formatarDataHora(ts: Long): String =
    SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.forLanguageTag("pt-BR")).format(Date(ts))
