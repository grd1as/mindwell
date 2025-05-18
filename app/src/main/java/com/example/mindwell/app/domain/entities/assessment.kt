package com.example.mindwell.app.domain.entities

import java.time.LocalDate

/**
 * Entidade que representa uma avaliação de saúde mental
 */
data class Assessment(
    val id: Long = 0,
    val date: LocalDate,
    val questions: List<AssessmentQuestion>,
    val completed: Boolean = false,
    val result: AssessmentResult? = null
)

/**
 * Entidade que representa uma pergunta de avaliação
 */
data class AssessmentQuestion(
    val id: String,
    val text: String,
    val type: QuestionType,
    val category: QuestionCategory,
    val required: Boolean = true,
    val answer: Any? = null
)

/**
 * Tipos de perguntas disponíveis
 */
enum class QuestionType {
    SCALE,
    YES_NO,
    TEXT,
    MULTIPLE_CHOICE
}

/**
 * Categorias de perguntas
 */
enum class QuestionCategory {
    WORKLOAD,
    AUTONOMY,
    RELATIONSHIPS,
    ROLE_CLARITY,
    CHANGE,
    SUPPORT,
    WORK_LIFE_BALANCE
}

/**
 * Resultado de uma avaliação
 */
data class AssessmentResult(
    val score: Int,
    val riskLevel: RiskLevel,
    val recommendations: List<String>
)

/**
 * Níveis de risco para saúde mental
 */
enum class RiskLevel {
    LOW,
    MEDIUM,
    HIGH
}