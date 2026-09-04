package com.example.studyflow.utils

import java.util.Calendar

/**
 * Calcula automaticamente a porcentagem de progresso da disciplina (retorna de 0f a 1f para o LinearProgressIndicator).
 *
 * Lógica: Iterar dia a dia a partir da dataInicio até a data de hoje, contando quantas vezes os dias da semana
 * agendados (ex: "SEG, QUA, SEX") já ocorreram. Em seguida, divide as "aulas já ocorridas" pelo totalAulas.
 */
fun calcularProgressoAutomatico(
    totalAulas: Int,
    diasSemanaStr: String,
    dataInicioMs: Long
): Float {
    if (totalAulas <= 0) return 0f

    // Configura o final do dia de hoje para incluir as aulas de hoje
    val hoje = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.timeInMillis

    if (dataInicioMs > hoje) return 0f // Aulas ainda não iniciaram

    // Converte a String de dias (ex: "SEG, QUA, SEX") para conjunto de inteiros Calendar.DAY_OF_WEEK
    val diasAgendados = diasSemanaStr.split(",")
        .map { it.trim().uppercase() }
        .mapNotNull { dia ->
            when {
                dia.contains("DOM") -> Calendar.SUNDAY
                dia.contains("SEG") -> Calendar.MONDAY
                dia.contains("TER") -> Calendar.TUESDAY
                dia.contains("QUA") -> Calendar.WEDNESDAY
                dia.contains("QUI") -> Calendar.THURSDAY
                dia.contains("SEX") -> Calendar.FRIDAY
                dia.contains("SAB") -> Calendar.SATURDAY
                else -> null
            }
        }.toSet()

    if (diasAgendados.isEmpty()) return 0f

    val cursor = Calendar.getInstance().apply {
        timeInMillis = dataInicioMs
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    var aulasOcorridas = 0

    // Itera dia a dia a partir da data de início até hoje
    while (cursor.timeInMillis <= hoje) {
        val diaSemanaAtual = cursor.get(Calendar.DAY_OF_WEEK)
        if (diaSemanaAtual in diasAgendados) {
            aulasOcorridas++
        }
        cursor.add(Calendar.DAY_OF_YEAR, 1)
    }

    return (aulasOcorridas.toFloat() / totalAulas).coerceIn(0f, 1f)
}
