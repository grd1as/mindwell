package com.example.mindwell.app.data.repositories

import com.example.mindwell.app.data.datasources.local.utils.PreferencesManager
import com.example.mindwell.app.domain.entities.OnboardingPage
import com.example.mindwell.app.domain.entities.OnboardingState
import com.example.mindwell.app.domain.repositories.OnboardingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do repositório de onboarding.
 */
@Singleton
class OnboardingRepositoryImpl @Inject constructor(
    private val preferences_manager: PreferencesManager
) : OnboardingRepository {
    
    // Estado atual do onboarding baseado no SharedPreferences
    private val _onboarding_state = MutableStateFlow(get_current_state())
    
    // Lista estática de páginas do onboarding (dados do aplicativo, não da API)
    private val onboarding_pages = listOf(
        OnboardingPage(
            id = 1,
            title = "Bem-vindo ao MindWell",
            description = "Uma jornada para o seu bem-estar mental começa aqui. Acompanhe sua saúde mental de forma simples e eficaz.",
            image_resource = "welcome"
        ),
        OnboardingPage(
            id = 2,
            title = "Check-ins Diários",
            description = "Registre como você está se sentindo diariamente e acompanhe seu humor ao longo do tempo.",
            image_resource = "checkin"
        ),
        OnboardingPage(
            id = 3,
            title = "Relatórios e Insights",
            description = "Obtenha relatórios semanais e mensais com insights valiosos sobre sua saúde mental.",
            image_resource = "reports"
        ),
        OnboardingPage(
            id = 4,
            title = "Recursos e Atividades",
            description = "Acesse recursos e atividades para ajudar a melhorar sua saúde mental e bem-estar.",
            image_resource = "resources"
        )
    )
    
    /**
     * Obtém o estado atual baseado no SharedPreferences
     */
    private fun get_current_state(): OnboardingState {
        return OnboardingState(
            is_completed = preferences_manager.isOnboardingCompleted()
        )
    }
    
    /**
     * Atualiza o estado interno do Flow
     */
    private fun update_state() {
        _onboarding_state.value = get_current_state()
    }
    
    override suspend fun get_onboarding_pages(): List<OnboardingPage> {
        return onboarding_pages
    }
    
    override fun get_onboarding_state(): Flow<OnboardingState> {
        return _onboarding_state.asStateFlow()
    }
    
    override suspend fun complete_onboarding() {
        preferences_manager.setOnboardingCompleted()
        preferences_manager.setFirstTimeComplete()
        update_state()
    }
    
    override suspend fun reset_onboarding() {
        preferences_manager.clearAll()
        update_state()
    }
    
    /**
     * Verifica se é a primeira vez que o usuário abre o app.
     * @return true se for a primeira vez, false caso contrário
     */
    fun is_first_time(): Boolean {
        return preferences_manager.isFirstTime()
    }
    
    /**
     * Marca que o usuário já abriu o app pela primeira vez.
     */
    fun set_first_time_complete() {
        preferences_manager.setFirstTimeComplete()
        update_state()
    }
} 