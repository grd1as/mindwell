package com.example.mindwell.app.domain.entities

import java.time.LocalDateTime

/**
 * Representa as configurações do usuário.
 */
data class UserPreferences(
    val id: Long = 1, // Singleton
    val notificationsEnabled: Boolean = true,
    val dataCollectionConsent: Boolean = false,
    val lastAssessmentReminder: LocalDateTime? = null
)