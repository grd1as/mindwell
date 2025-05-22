package com.example.mindwell.app.domain.entities

/**
 * Entidade de domínio representando uma opção de resposta para uma pergunta.
 */
data class Option(
    val id: Int,
    val value: String,
    val label: String
) 