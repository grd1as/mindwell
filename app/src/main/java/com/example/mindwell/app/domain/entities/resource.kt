package com.example.mindwell.app.domain.entities

/**
 * Entidade de domínio representando um recurso.
 */
data class Resource(
    val id: String,
    val title: String,
    val description: String,
    val categoryId: String,
    val durationMinutes: Int
)

/**
 * Entidade de domínio representando detalhes de um recurso.
 */
data class ResourceDetail(
    val id: String,
    val title: String,
    val description: String,
    val categoryId: String,
    val durationMinutes: Int,
    val steps: List<String> = emptyList(),
    val completed: Boolean = false
)

/**
 * Entidade de domínio representando categoria de recurso.
 */
data class ResourceCategory(
    val id: String,
    val title: String,
    val description: String
) 