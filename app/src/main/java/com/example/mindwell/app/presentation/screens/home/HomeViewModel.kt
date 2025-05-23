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
import com.example.mindwell.app.domain.entities.Checkin
import com.example.mindwell.app.domain.entities.Form
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
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
        val isLoading: Boolean = false,
        val userName: String = "Usuário",
        val lastCheckin: String = "",
        val pendingForms: Int = 2,
        val streakCount: Int = 0,
        val error: String? = null,
        val showFeedbackDialog: Boolean = false,
        val feedbackCategory: String = "",
        val feedbackDescription: String = "",
        val isSubmittingFeedback: Boolean = false,
        val feedbackSuccess: Boolean = false,
        val feedbackError: String? = null,
        val checkInSuccess: Boolean = false,
        val checkInError: String? = null
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
        state = state.copy(isLoading = true)
        
        // Simulação de carregamento de dados
        viewModelScope.launch {
            try {
                delay(500) // Simula requisição de rede
                state = state.copy(
                    isLoading = false,
                    userName = "Karina Santos",
                    pendingForms = 2
                )
            } catch (e: Exception) {
                state = state.copy(
                    isLoading = false
                )
            }
        }
    }
    
    private fun loadCheckinData() {
        viewModelScope.launch {
            // Get last check-in
            getLastCheckin()
                .catch { e: Throwable -> 
                    state = state.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
                .collect { result ->
                    result.onSuccess { checkin: Checkin ->
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
                .catch { e: Throwable ->
                    state = state.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
                .collect { result ->
                    result.onSuccess { forms: List<Form> ->
                        state = state.copy(
                            pendingForms = forms.size,
                            isLoading = false
                        )
                    }
                    result.onFailure { e: Throwable ->
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
        if (state.feedbackCategory.isEmpty()) {
            state = state.copy(
                feedbackError = "Por favor, selecione uma categoria."
            )
            return
        }
        
        if (state.feedbackDescription.length < 10) {
            state = state.copy(
                feedbackError = "A descrição deve ter pelo menos 10 caracteres."
            )
            return
        }
        
        state = state.copy(
            isSubmittingFeedback = true,
            feedbackError = null
        )
        
        // Simulação de envio
        viewModelScope.launch {
            try {
                delay(1500) // Simula requisição de rede
                state = state.copy(
                    isSubmittingFeedback = false,
                    feedbackSuccess = true
                )
                
                // Fecha o diálogo após alguns segundos
                delay(2000)
                state = state.copy(showFeedbackDialog = false)
            } catch (e: Exception) {
                state = state.copy(
                    isSubmittingFeedback = false,
                    feedbackError = "Erro ao enviar feedback. Por favor, tente novamente."
                )
            }
        }
    }
    
    /**
     * Enviar check-in com emoji e sentimento
     */
    fun submitCheckin(emoji: String, feeling: String) {
        viewModelScope.launch {
            try {
                // Simulação de envio
                delay(1000)
                
                // Sucesso
                state = state.copy(
                    checkInSuccess = true,
                    checkInError = null
                )
            } catch (e: Exception) {
                state = state.copy(
                    checkInSuccess = false,
                    checkInError = "Erro ao enviar check-in. Tente novamente."
                )
            }
        }
    }
    
    /**
     * Inicia um questionário específico
     */
    fun startQuestionnaire(code: String) {
        // Implementar navegação ou lógica para iniciar o questionário
        viewModelScope.launch {
            // Simulação de início de questionário
            delay(500)
            
            // Aqui seria a navegação para o questionário específico
        }
    }
    
    /**
     * Seleciona formulário de relatório
     */
    fun selectReportForm(code: String) {
        if (code == "REPORT") {
            // Mostrar diálogo de feedback para relatório
            showFeedbackDialog()
        }
    }
} 