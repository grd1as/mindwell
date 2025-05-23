package com.example.mindwell.app.domain.entities

/**
 * Entidade de domínio representando uma resposta para uma pergunta.
 */
data class Answer(
    val question_id: Int,
    val option_id: Int? = null,     // Nullable para perguntas de texto
    val text_response: String? = null  // Para perguntas de texto livre
) 