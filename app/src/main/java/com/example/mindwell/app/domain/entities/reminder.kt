package com.example.mindwell.app.domain.entities

import java.time.ZonedDateTime

/**
 * Entidade de domínio representando um lembrete.
 */
data class Reminder(
    val form_id: Int,
    val title: String,
    val scheduled: ZonedDateTime
) 