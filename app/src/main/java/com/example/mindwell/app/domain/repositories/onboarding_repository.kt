package com.example.mindwell.app.domain.repositories

import com.example.mindwell.app.domain.entities.OnboardingPage
import com.example.mindwell.app.domain.entities.OnboardingState
import kotlinx.coroutines.flow.Flow

/**
 * Repositório para gerenciar o onboarding.
 */
interface OnboardingRepository {
    /**
     * Obtém todas as páginas do onboarding.
     * @return Lista de páginas do onboarding
     */
    suspend fun get_onboarding_pages(): List<OnboardingPage>
    
    /**
     * Obtém o estado atual do onboarding.
     * @return Flow com o estado atual do onboarding
     */
    fun get_onboarding_state(): Flow<OnboardingState>
    
    /**
     * Marca o onboarding como concluído.
     */
    suspend fun complete_onboarding()
    
    /**
     * Reseta o estado do onboarding, marcando-o como não concluído.
     */
    suspend fun reset_onboarding()
} 