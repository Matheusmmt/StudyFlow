package com.example.studyflow.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeletorData(aoSelecionar: (Long) -> Unit, aoFechar: () -> Unit) {
    val estado = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = aoFechar,
        confirmButton = {
            TextButton(onClick = {
                estado.selectedDateMillis?.let(aoSelecionar)
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
        initialHour = agora.get(Calendar.HOUR_OF_DAY),
        initialMinute = agora.get(Calendar.MINUTE),
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
    SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(Date(ts))
fun formatarDataHora(ts: Long): String =
    SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale("pt", "BR")).format(Date(ts))
