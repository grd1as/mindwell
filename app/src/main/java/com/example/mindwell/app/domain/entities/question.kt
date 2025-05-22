package com.example.mindwell.app.domain.entities

/**
 * Entidade de domínio representando uma pergunta de formulário.
 */
data class Question(
    val id: Int,
    val ordinal: Int,
    val type: String,
    val text: String,
    val options: List<Option>
) 