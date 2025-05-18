package com.example.mindwell.app.common.navigation

/**
 * Objeto com todas as rotas/destinos do aplicativo.
 */
object AppDestinations {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val CHECK_IN = "check-in"
    const val ASSESSMENT = "assessment"
    const val RESOURCES = "resources"
    const val METRICS = "metrics"
    
    // Destinos com parâmetros
    const val CHECK_IN_DETAIL = "check-in/{checkInId}"
    const val ASSESSMENT_DETAIL = "assessment/{assessmentId}"
    const val RESOURCE_DETAIL = "resource/{resourceId}"
} 