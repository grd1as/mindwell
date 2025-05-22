package com.example.mindwell.app.data.repositories

import com.example.mindwell.app.domain.entities.OnboardingPage
import com.example.mindwell.app.domain.entities.OnboardingState
import com.example.mindwell.app.domain.repositories.OnboardingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do repositório de onboarding.
 */
@Singleton
class OnboardingRepositoryImpl @Inject constructor() : OnboardingRepository {
    
    // Estado atual do onboarding (inicialmente não concluído)
    private val onboardingState = MutableStateFlow(OnboardingState(isCompleted = false))
    
    // Lista de páginas do onboarding
    private val onboardingPages = listOf(
        OnboardingPage(
            id = 1,
            title = "Bem-vindo ao MindWell",
            description = "Uma jornada para o seu bem-estar mental começa aqui. Acompanhe sua saúde mental de forma simples e eficaz.",
            imageResource = "welcome"
        ),
        OnboardingPage(
            id = 2,
            title = "Check-ins Diários",
            description = "Registre como você está se sentindo diariamente e acompanhe seu humor ao longo do tempo.",
            imageResource = "checkin"
        ),
        OnboardingPage(
            id = 3,
            title = "Relatórios e Insights",
            description = "Obtenha relatórios semanais e mensais com insights valiosos sobre sua saúde mental.",
            imageResource = "reports"
        ),
        OnboardingPage(
            id = 4,
            title = "Recursos e Atividades",
            description = "Acesse recursos e atividades para ajudar a melhorar sua saúde mental e bem-estar.",
            imageResource = "resources"
        )
    )
    
    override suspend fun getOnboardingPages(): List<OnboardingPage> {
        return onboardingPages
    }
    
    override fun getOnboardingState(): Flow<OnboardingState> {
        return onboardingState
    }
    
    override suspend fun completeOnboarding() {
        onboardingState.value = OnboardingState(isCompleted = true)
    }
    
    override suspend fun resetOnboarding() {
        onboardingState.value = OnboardingState(isCompleted = false)
    }
} 