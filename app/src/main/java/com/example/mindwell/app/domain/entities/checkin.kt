package com.example.mindwell.app.domain.entities

import java.time.LocalDateTime

/**
 * Representa um registro diário de bem-estar do usuário.
 */
data class CheckIn(
    val id: Long = 0,
    val timestamp: LocalDateTime,
    val moodLevel: Int, // Escala 1-5
    val stressLevel: Int, // Escala 1-5
    val notes: String? = null
) {
    companion object {
        const val MIN_LEVEL = 1
        const val MAX_LEVEL = 5
    }
    
    init {
        require(moodLevel in MIN_LEVEL..MAX_LEVEL) { "Mood level must be between $MIN_LEVEL and $MAX_LEVEL" }
        require(stressLevel in MIN_LEVEL..MAX_LEVEL) { "Stress level must be between $MIN_LEVEL and $MAX_LEVEL" }
    }
}