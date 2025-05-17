package com.example.mindwell.app.domain.entities

import java.time.LocalDate

/**
 * Representa métricas agregadas de bem-estar para um determinado período.
 */
data class WellbeingMetrics(
    val date: LocalDate,
    val averageMood: Float,
    val averageStress: Float,
    val workloadScore: Int? = null,
    val environmentScore: Int? = null,
    val wellbeingScore: Float // Pontuação composta
) {
    companion object {
        const val MIN_SCORE = 0f
        const val MAX_SCORE = 100f
    }
    
    init {
        require(averageMood in 1f..5f) { "Average mood must be between 1 and 5" }
        require(averageStress in 1f..5f) { "Average stress must be between 1 and 5" }
        require(wellbeingScore in MIN_SCORE..MAX_SCORE) { "Wellbeing score must be between $MIN_SCORE and $MAX_SCORE" }
        workloadScore?.let { require(it in 0..100) { "Workload score must be between 0 and 100" } }
        environmentScore?.let { require(it in 0..100) { "Environment score must be between 0 and 100" } }
    }
    
    /**
     * Determina se o bem-estar está em um nível crítico que exige atenção.
     */
    fun isCritical(): Boolean {
        return wellbeingScore < 30f || averageStress > 4f || averageMood < 2f
    }
    
    /**
     * Determina se o bem-estar está em um nível que requer atenção.
     */
    fun needsAttention(): Boolean {
        return wellbeingScore < 50f || averageStress > 3f || averageMood < 3f
    }
}