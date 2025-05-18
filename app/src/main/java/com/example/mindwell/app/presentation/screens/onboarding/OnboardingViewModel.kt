package com.example.mindwell.app.presentation.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindwell.app.domain.entities.AnonymousUser
import com.example.mindwell.app.domain.usecases.userpreferences.CompleteOnboardingUseCase
import com.example.mindwell.app.domain.usecases.userpreferences.GetAnonymousUserUseCase
import com.example.mindwell.app.domain.usecases.userpreferences.MockCompleteOnboardingUseCase
import com.example.mindwell.app.domain.usecases.userpreferences.UpdateDataConsentUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado da UI para as telas de onboarding.
 */
data class OnboardingUiState(
    val currentPage: Int = 0,
    val totalPages: Int = 3,
    val anonymousUser: AnonymousUser? = null,
    val hasCompletedOnboarding: Boolean = false,
    val hasAcceptedConsent: Boolean = false,
    val isLoading: Boolean = true
)

/**
 * Mock implementação do caso de uso para obter usuário anônimo
 */
class MockGetAnonymousUserUseCase : GetAnonymousUserUseCase {
    override fun invoke(): Flow<AnonymousUser> = flow {
        // Simular atraso
        kotlinx.coroutines.delay(500)
        emit(AnonymousUser())
    }
}

/**
 * Mock implementação do caso de uso para atualizar consentimento
 */
class MockUpdateDataConsentUseCase : UpdateDataConsentUseCase {
    override suspend fun invoke(hasConsent: Boolean) {
        // Simular atraso
        kotlinx.coroutines.delay(300)
    }
}

/**
 * ViewModel para as telas de onboarding.
 */
class OnboardingViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(OnboardingUiState(isLoading = true))
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()
    
    // Mock implementações
    private val getAnonymousUserUseCase: GetAnonymousUserUseCase = MockGetAnonymousUserUseCase()
    private val updateDataConsentUseCase: UpdateDataConsentUseCase = MockUpdateDataConsentUseCase()
    private val completeOnboardingUseCase: CompleteOnboardingUseCase = MockCompleteOnboardingUseCase()
    
    init {
        loadAnonymousUser()
    }
    
    /**
     * Carrega o usuário anônimo.
     */
    private fun loadAnonymousUser() {
        viewModelScope.launch {
            try {
                val user = getAnonymousUserUseCase().firstOrNull()
                
                _uiState.update { 
                    it.copy(
                        anonymousUser = user,
                        hasAcceptedConsent = user?.consentedToDataCollection ?: false,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
    
    /**
     * Atualiza a página atual.
     */
    fun updateCurrentPage(page: Int) {
        _uiState.update { it.copy(currentPage = page) }
    }
    
    /**
     * Avança para a próxima página ou conclui o onboarding.
     */
    fun nextPage() {
        val currentPage = _uiState.value.currentPage
        val totalPages = _uiState.value.totalPages
        
        if (currentPage < totalPages - 1) {
            _uiState.update { it.copy(currentPage = it.currentPage + 1) }
        } else {
            completeOnboarding()
        }
    }
    
    /**
     * Volta para a página anterior.
     */
    fun previousPage() {
        if (_uiState.value.currentPage > 0) {
            _uiState.update { it.copy(currentPage = it.currentPage - 1) }
        }
    }
    
    /**
     * Atualiza o status de consentimento para coleta de dados.
     */
    fun updateConsent(hasConsent: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(hasAcceptedConsent = hasConsent) }
            updateDataConsentUseCase(hasConsent)
        }
    }
    
    /**
     * Marca o onboarding como concluído.
     */
    fun completeOnboarding() {
        viewModelScope.launch {
            completeOnboardingUseCase()
            _uiState.update { it.copy(hasCompletedOnboarding = true) }
        }
    }
    
    /**
     * Pula o onboarding e vai direto para o aplicativo.
     */
    fun skipOnboarding() {
        viewModelScope.launch {
            completeOnboardingUseCase()
            _uiState.update { it.copy(hasCompletedOnboarding = true) }
        }
    }
} 