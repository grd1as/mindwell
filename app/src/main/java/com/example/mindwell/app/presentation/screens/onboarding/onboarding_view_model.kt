package com.example.mindwell.app.presentation.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindwell.app.domain.entities.OnboardingPage
import com.example.mindwell.app.domain.usecases.onboarding.CompleteOnboardingUseCase
import com.example.mindwell.app.domain.usecases.onboarding.GetOnboardingPagesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado da UI para a tela de onboarding.
 */
data class OnboardingUiState(
    val pages: List<OnboardingPage> = emptyList(),
    val currentPage: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel para a tela de onboarding.
 */
@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val getOnboardingPagesUseCase: GetOnboardingPagesUseCase,
    private val completeOnboardingUseCase: CompleteOnboardingUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(OnboardingUiState(isLoading = true))
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()
    
    init {
        loadOnboardingPages()
    }
    
    /**
     * Carrega as páginas do onboarding.
     */
    private fun loadOnboardingPages() {
        viewModelScope.launch {
            getOnboardingPagesUseCase().collectLatest { result ->
                result.fold(
                    onSuccess = { pages ->
                        // Limitamos a apenas 2 páginas de onboarding
                        val onboardingPages = if (pages.size > 2) pages.take(2) else pages
                        _uiState.update { state ->
                            state.copy(
                                pages = onboardingPages,
                                isLoading = false,
                                error = null
                            )
                        }
                    },
                    onFailure = { exception ->
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                error = exception.message
                            )
                        }
                    }
                )
            }
        }
    }
    
    /**
     * Avança para a próxima página do onboarding.
     * @return true se avançou para a próxima página, false se já está na última página
     */
    fun moveToNextPage(): Boolean {
        val currentPage = _uiState.value.currentPage
        val pagesCount = _uiState.value.pages.size
        
        if (currentPage < pagesCount - 1) {
            _uiState.update { state ->
                state.copy(currentPage = currentPage + 1)
            }
            return true
        }
        return false
    }
    
    /**
     * Retorna para a página anterior do onboarding.
     * @return true se voltou para a página anterior, false se já está na primeira página
     */
    fun moveToPreviousPage(): Boolean {
        val currentPage = _uiState.value.currentPage
        
        if (currentPage > 0) {
            _uiState.update { state ->
                state.copy(currentPage = currentPage - 1)
            }
            return true
        }
        
        return false
    }
    
    /**
     * Marca o onboarding como concluído.
     */
    fun completeOnboarding() {
        viewModelScope.launch {
            completeOnboardingUseCase().collectLatest { result ->
                result.fold(
                    onSuccess = {
                        // Onboarding concluído com sucesso, navegará para tela de login
                    },
                    onFailure = { exception ->
                        _uiState.update { state ->
                            state.copy(error = exception.message)
                        }
                    }
                )
            }
        }
    }
    
    /**
     * Navega diretamente para uma página específica.
     * @param pageIndex Índice da página para a qual deseja navegar
     */
    fun navigateToPage(pageIndex: Int) {
        if (pageIndex in 0 until _uiState.value.pages.size) {
            _uiState.update { state ->
                state.copy(currentPage = pageIndex)
            }
        }
    }
} 