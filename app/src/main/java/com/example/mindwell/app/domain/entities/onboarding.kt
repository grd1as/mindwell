package com.example.mindwell.app.domain.entities

/**
 * Entidade de domínio representando uma página do onboarding.
 */
data class OnboardingPage(
    val id: Int,
    val title: String,
    val description: String,
    val image_resource: String
)

/**
 * Entidade de domínio representando o estado do onboarding.
 */
data class OnboardingState(
    val is_completed: Boolean = false
) 