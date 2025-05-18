package com.example.mindwell.app.domain.entities

import java.util.UUID

/**
 * Representa um usuário anônimo no sistema.
 * Não contém informações pessoais identificáveis.
 */
data class AnonymousUser(
    val deviceId: String = UUID.randomUUID().toString(),
    val preferredReminderTime: String? = null,
    val notificationsEnabled: Boolean = true,
    val lastActiveDate: Long = System.currentTimeMillis(),
    val consentedToDataCollection: Boolean = false,
    val appTheme: AppTheme = AppTheme.SYSTEM
)

/**
 * Temas disponíveis no aplicativo
 */
enum class AppTheme {
    LIGHT, DARK, SYSTEM
} 