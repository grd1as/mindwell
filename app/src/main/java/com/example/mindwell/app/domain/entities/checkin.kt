package com.example.mindwell.app.domain.entities

import java.time.ZonedDateTime

/**
 * Entidade de domínio representando um check-in completo.
 */
data class Checkin(
    val id: Long = 0,
    val date: String,
    val emotion: Emotion,
    val note: String? = null,
    val streak: Int? = null
)

/**
 * Entidade de domínio representando uma resposta de check-in.
 */
data class CheckinAnswer(
    val question_id: Int,
    val option_id: Int,
    val value: String
) 