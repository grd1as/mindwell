package com.example.mindwell.app.domain.entities

import java.time.LocalDateTime

/**
 * Representa uma avaliação de risco psicossocial completa.
 */
data class Assessment(
    val id: Long = 0,
    val type: AssessmentType,
    val timestamp: LocalDateTime,
    val responses: Map<String, Int>, // questão -> resposta (1-5)
    val score: Int // pontuação calculada
) {
    companion object {
        const val MIN_RESPONSE = 1
        const val MAX_RESPONSE = 5
        const val MIN_SCORE = 0
        const val MAX_SCORE = 100
    }
    
    init {
        require(responses.isNotEmpty()) { "Assessment must have at least one response" }
        require(responses.all { it.value in MIN_RESPONSE..MAX_RESPONSE }) { 
            "All responses must be between $MIN_RESPONSE and $MAX_RESPONSE" 
        }
        require(score in MIN_SCORE..MAX_SCORE) { "Score must be between $MIN_SCORE and $MAX_SCORE" }
    }
}

/**
 * Tipos de avaliação disponíveis no sistema.
 */
enum class AssessmentType {
    WORK_ENVIRONMENT, // Ambiente de trabalho
    WORKLOAD,         // Carga de trabalho
    STRESS            // Estresse
}