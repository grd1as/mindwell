package com.example.mindwell.app.domain.entities

/**
 * Representa um recurso educativo ou de apoio à saúde mental.
 */
data class Resource(
    val id: Long = 0,
    val title: String,
    val description: String,
    val type: ResourceType,
    val tags: List<String>,
    val content: String,
    val isRecommended: Boolean = false
) {
    init {
        require(title.isNotBlank()) { "Resource title cannot be blank" }
        require(content.isNotBlank()) { "Resource content cannot be blank" }
    }
}

/**
 * Tipos de recursos disponíveis no sistema.
 */
enum class ResourceType {
    MINDFULNESS, // Exercícios de mindfulness
    EDUCATION,   // Conteúdo educativo
    EXERCISE     // Exercícios práticos
}