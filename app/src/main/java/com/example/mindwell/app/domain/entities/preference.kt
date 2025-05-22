package com.example.mindwell.app.domain.entities

/**
 * Entidade de domínio representando as preferências do usuário.
 */
data class Preference(
    val name: String = "",
    val notificationsEnabled: Boolean = false
) 