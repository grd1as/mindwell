package com.example.mindwell.app.domain.entities

import java.time.LocalDateTime

/**
 * Representa um lembrete ou dica personalizada.
 */
data class Reminder(
    val id: Long = 0,
    val title: String,
    val message: String,
    val type: ReminderType,
    val scheduledTime: LocalDateTime,
    val delivered: Boolean = false,
    val priority: ReminderPriority = ReminderPriority.MEDIUM,
    val tags: List<String> = emptyList(),
    val associatedResourceId: Long? = null // Opcional: ID de um recurso relacionado
)

/**
 * Tipos de lembretes disponíveis.
 */
enum class ReminderType {
    CHECKIN,      // Lembrete para fazer check-in diário
    ASSESSMENT,   // Lembrete para completar avaliação
    WELLBEING_TIP, // Dica de bem-estar
    RESOURCE,     // Recurso recomendado
    CUSTOM        // Lembrete personalizado
}

/**
 * Prioridades para lembretes.
 */
enum class ReminderPriority {
    LOW,
    MEDIUM,
    HIGH
} 