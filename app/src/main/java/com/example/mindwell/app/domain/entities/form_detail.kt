package com.example.mindwell.app.domain.entities

/**
 * Entidade de domínio representando os detalhes de um formulário com suas perguntas.
 */
data class FormDetail(
    val id: Int,
    val code: String,
    val name: String,
    val questions: List<Question>
) 