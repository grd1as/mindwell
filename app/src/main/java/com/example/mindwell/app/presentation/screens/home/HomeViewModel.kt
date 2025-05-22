package com.example.mindwell.app.presentation.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindwell.app.data.model.ReportDTO
import com.example.mindwell.app.data.network.ApiService
import com.example.mindwell.app.domain.usecases.checkin.GetLastCheckinUseCase
import com.example.mindwell.app.domain.usecases.form.GetPendingFormsUseCase
import com.example.mindwell.app.domain.usecases.preference.GetUserPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para tela principal (Home).
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserPreferences: GetUserPreferencesUseCase,
    private val getLastCheckin: GetLastCheckinUseCase,
    private val getPendingForms: GetPendingFormsUseCase,
    private val apiService: ApiService
) : ViewModel() {
    // Estado da tela home
    data class HomeState(
        val isLoading: Boolean = true,
        val userName: String = "",
        val lastCheckin: String = "",
        val pendingForms: Int = 0,
        val streakCount: Int = 0,
        val error: String? = null,
        val showFeedbackDialog: Boolean = false,
        val feedbackCategory: String = "",
        val feedbackDescription: String = "",
        val isSubmittingFeedback: Boolean = false,
        val feedbackSuccess: Boolean = false,
        val feedbackError: String? = null
    )
    
    // Estado atual da tela
    var state by mutableStateOf(HomeState())
        private set
    
    init {
        loadData()
    }

    /**
     * Lista de categorias de feedback disponíveis
     */
    val feedbackCategories = listOf(
        "ASSÉDIO_MORAL",
        "ASSÉDIO_SEXUAL",
        "DISCRIMINAÇÃO_RACIAL",
        "DISCRIMINAÇÃO_DE_GÊNERO",
        "VIOLÊNCIA_FÍSICA",
        "VIOLÊNCIA_VERBAL",
        "CONFLITO_INTERPESSOAL",
        "SAÚDE_E_SEGURANÇA",
        "INFRAESTRUTURA_INADEQUADA",
        "EQUIPAMENTO_QUEBRADO",
        "ERGONOMIA_INADEQUADA",
        "OUTRO"
    )
    
    /**
     * Carrega dados para a tela home.
     */
    private fun loadData() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            
            // Get user preferences
            getUserPreferences()
                .catch { e ->
                    state = state.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
                .collect { result ->
                    result.onSuccess { preferences ->
                        state = state.copy(
                            userName = preferences.name
                        )
                    }
                    result.onFailure { e ->
                        state = state.copy(
                            error = e.message
                        )
                    }
                    
                    // Continue loading other data
                    loadCheckinData()
                }
        }
    }
    
    private fun loadCheckinData() {
        viewModelScope.launch {
            // Get last check-in
            getLastCheckin()
                .catch { e ->
                    state = state.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
                .collect { result ->
                    result.onSuccess { checkin ->
                        state = state.copy(
                            lastCheckin = checkin.date,
                            streakCount = checkin.streak ?: 0
                        )
                    }
                    
                    // Continue with forms data
                    loadFormsData()
                }
        }
    }
    
    private fun loadFormsData() {
        viewModelScope.launch {
            // Get pending forms
            getPendingForms()
                .catch { e ->
                    state = state.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
                .collect { result ->
                    result.onSuccess { forms ->
                        state = state.copy(
                            pendingForms = forms.size,
                            isLoading = false
                        )
                    }
                    result.onFailure { e ->
                        state = state.copy(
                            isLoading = false,
                            error = e.message
                        )
                    }
                }
        }
    }
    
    fun refresh() {
        loadData()
    }
    
    /**
     * Mostra o diálogo de feedback
     */
    fun showFeedbackDialog() {
        state = state.copy(
            showFeedbackDialog = true,
            feedbackCategory = "",
            feedbackDescription = "",
            feedbackSuccess = false,
            feedbackError = null
        )
    }
    
    /**
     * Esconde o diálogo de feedback
     */
    fun hideFeedbackDialog() {
        state = state.copy(
            showFeedbackDialog = false
        )
    }
    
    /**
     * Atualiza a categoria de feedback selecionada
     */
    fun updateFeedbackCategory(category: String) {
        state = state.copy(
            feedbackCategory = category
        )
    }
    
    /**
     * Atualiza a descrição do feedback
     */
    fun updateFeedbackDescription(description: String) {
        state = state.copy(
            feedbackDescription = description
        )
    }
    
    /**
     * Envia o feedback para a API
     */
    fun submitFeedback() {
        if (state.feedbackCategory.isBlank()) {
            state = state.copy(
                feedbackError = "Por favor, selecione uma categoria"
            )
            return
        }
        
        viewModelScope.launch {
            state = state.copy(
                isSubmittingFeedback = true,
                feedbackError = null
            )
            
            try {
                val report = ReportDTO(
                    category = state.feedbackCategory,
                    description = state.feedbackDescription,
                    tags = listOf("APP_MOBILE")
                )
                
                val response = apiService.submitReport(report)
                
                state = state.copy(
                    isSubmittingFeedback = false,
                    feedbackSuccess = true,
                    feedbackError = null
                )
                
                // Fechar o diálogo após sucesso (depois de 1.5 segundos)
                kotlinx.coroutines.delay(1500)
                hideFeedbackDialog()
                
            } catch (e: Exception) {
                state = state.copy(
                    isSubmittingFeedback = false,
                    feedbackSuccess = false,
                    feedbackError = e.message ?: "Erro ao enviar feedback"
                )
            }
        }
    }
} 