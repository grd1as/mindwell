package com.example.mindwell.app.domain.entities

import java.time.ZonedDateTime

/**
 * Entidade de domínio representando um formulário.
 */
data class Form(
    val id: Int,
    val code: String,
    val name: String,
    val type: String,
    val description: String,
    val nextAllowed: ZonedDateTime?,
    val lastAnsweredAt: ZonedDateTime?
) 