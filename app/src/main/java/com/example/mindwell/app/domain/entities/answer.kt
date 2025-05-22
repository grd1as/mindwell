package com.example.mindwell.app.domain.entities

/**
 * Entidade de domínio representando uma resposta para uma pergunta.
 */
data class Answer(
    val questionId: Int,
    val optionId: Int
) 