package com.example.mindwell.app.domain.entities

/**
 * Entidade de domínio representando um sentimento disponível para check-in.
 */
data class Feeling(
    val id: String,
    val label: String,
    val emoji: String? = null,
    val value: Int? = null
) 