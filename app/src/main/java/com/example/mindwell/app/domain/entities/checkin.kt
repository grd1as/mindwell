package com.example.mindwell.app.domain.entities

import java.time.ZonedDateTime

/**
 * Entidade de domínio representando um check-in completo com suas respostas.
 */
data class Checkin(
    val checkinId: Int,
    val timestamp: ZonedDateTime,
    val answers: List<CheckinAnswer>,
    val streak: Int? = null,
    val date: String = "" // Formato amigável para exibição
)

/**
 * Entidade de domínio representando uma resposta de check-in.
 */
data class CheckinAnswer(
    val questionId: Int,
    val optionId: Int,
    val value: String
) 