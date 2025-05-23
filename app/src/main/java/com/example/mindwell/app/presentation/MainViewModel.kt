package com.example.mindwell.app.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindwell.app.common.navigation.AppDestinations
import com.example.mindwell.app.domain.usecases.auth.CheckAuthStatusUseCase
import com.example.mindwell.app.domain.usecases.onboarding.GetOnboardingStateUseCase
import com.example.mindwell.app.domain.usecases.onboarding.IsFirstTimeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado da navegação inicial
 */
sealed class NavigationState {
    object Loading : NavigationState()
    data class Ready(val destination: String) : NavigationState()
    data class Error(val message: String) : NavigationState()
}

/**
 * ViewModel principal para gerenciar a navegação inicial do app.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val is_first_time_use_case: IsFirstTimeUseCase,
    private val get_onboarding_state_use_case: GetOnboardingStateUseCase,
    private val check_auth_status_use_case: CheckAuthStatusUseCase
) : ViewModel() {
    
    var navigation_state by mutableStateOf<NavigationState>(NavigationState.Loading)
        private set
    
    init {
        determine_start_destination()
    }
    
    /**
     * Determina a tela inicial baseada no estado do usuário.
     * Ordem de prioridade:
     * 1. Se for primeira vez -> Onboarding
     * 2. Se onboarding não foi completado -> Onboarding
     * 3. Se usuário não está autenticado -> Login
     * 4. Se usuário está autenticado -> Home
     */
    private fun determine_start_destination() {
        viewModelScope.launch {
            try {
                // Primeiro verifica se é a primeira vez
                val first_time_result = is_first_time_use_case().first()
                val is_first_time = first_time_result.getOrNull() ?: true
                
                if (is_first_time) {
                    navigation_state = NavigationState.Ready(AppDestinations.ONBOARDING)
                    return@launch
                }
                
                // Verifica se o onboarding foi completado
                val onboarding_state_result = get_onboarding_state_use_case().first()
                val is_onboarding_completed = onboarding_state_result.getOrNull()?.is_completed ?: false
                
                if (!is_onboarding_completed) {
                    navigation_state = NavigationState.Ready(AppDestinations.ONBOARDING)
                    return@launch
                }
                
                // Verifica se o usuário está autenticado
                val auth_status_result = check_auth_status_use_case().first()
                val is_authenticated = auth_status_result.getOrNull() ?: false
                
                navigation_state = if (is_authenticated) {
                    NavigationState.Ready(AppDestinations.HOME)
                } else {
                    NavigationState.Ready(AppDestinations.LOGIN)
                }
                
            } catch (e: Exception) {
                // Em caso de erro, sempre começar pelo onboarding por segurança
                navigation_state = NavigationState.Error("Erro ao determinar tela inicial: ${e.message}")
                // Fallback para onboarding
                navigation_state = NavigationState.Ready(AppDestinations.ONBOARDING)
            }
        }
    }
    
    /**
     * Força uma nova verificação da navegação inicial
     */
    fun refresh_navigation_state() {
        navigation_state = NavigationState.Loading
        determine_start_destination()
    }
} 