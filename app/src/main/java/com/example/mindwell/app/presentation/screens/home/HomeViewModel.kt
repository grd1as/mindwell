package com.example.mindwell.app.presentation.screens.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.mindwell.app.common.navigation.AppDestinations
import com.example.mindwell.app.data.model.ReportDTO
import com.example.mindwell.app.data.network.ApiService
import com.example.mindwell.app.domain.usecases.checkin.GetLastCheckinUseCase
import com.example.mindwell.app.domain.usecases.form.GetPendingFormsUseCase
import com.example.mindwell.app.domain.usecases.preference.GetUserPreferencesUseCase
import com.example.mindwell.app.domain.usecases.feeling.GetFeelingsUseCase
import com.example.mindwell.app.domain.entities.Checkin
import com.example.mindwell.app.domain.entities.Form
import com.example.mindwell.app.domain.entities.Feeling
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

/**
 * ViewModel para tela principal (Home).
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserPreferences: GetUserPreferencesUseCase,
    private val getLastCheckin: GetLastCheckinUseCase,
    private val getPendingForms: GetPendingFormsUseCase,
    private val getFeelings: GetFeelingsUseCase,
    private val apiService: ApiService
) : ViewModel() {
    private val TAG = "HomeViewModel"
    
    // Eventos de navegação
    sealed class NavigationEvent {
        data class ToForm(val formId: Int) : NavigationEvent()
        data class ToResource(val resourceId: String) : NavigationEvent()
        object ToForms : NavigationEvent()
        object Handled : NavigationEvent()
    }
    
    // Classe para representar uma dica personalizada
    data class CustomTip(
        val id: String,
        val title: String,
        val description: String,
        val iconId: Int = 0
    )
    
    // Estado da tela home
    data class HomeState(
        val isLoading: Boolean = false,
        val userName: String = "Usuário",
        val lastCheckin: String = "",
        val pendingForms: Int = 0,
        val streakCount: Int = 0,
        val error: String? = null,
        val showFeedbackDialog: Boolean = false,
        val feedbackCategory: String = "",
        val feedbackDescription: String = "",
        val isSubmittingFeedback: Boolean = false,
        val feedbackSuccess: Boolean = false,
        val feedbackError: String? = null,
        val checkInSuccess: Boolean = false,
        val checkInError: String? = null,
        val navigationEvent: NavigationEvent? = null,
        val activeTooltip: String? = null,
        val customTips: List<CustomTip> = emptyList(),
        val greeting: String = "",
        val greetingEmoji: String = "",
        val feelings: List<Feeling> = emptyList()
    )
    
    // Estado atual da tela
    var state by mutableStateOf(HomeState())
        private set
    
    // NavController para navegação
    private var navController: NavController? = null
    
    init {
        loadData()
        loadCustomTips()
        updateGreeting()
    }
    
    /**
     * Configura o NavController para navegação
     */
    fun setNavController(nav: NavController) {
        this.navController = nav
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
        
        Log.d(TAG, "🌐 Tentando carregar dados da home da API")
        
        viewModelScope.launch {
            try {
                // Carregar dados do usuário
                loadUserData()
                
                // Carregar último check-in
                loadCheckinData()
                
                // Carregar formulários pendentes  
                loadFormsData()
                
                // Carregar sentimentos
                loadFeelingsData()
                
                Log.d(TAG, "✅ Sucesso ao carregar dados da home")
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ ERRO ao carregar dados da home: ${e.message}", e)
                state = state.copy(
                    isLoading = false,
                    error = "Erro ao carregar dados: ${e.message}"
                )
            }
        }
    }
    
    private fun loadUserData() {
        viewModelScope.launch {
            getUserPreferences()
                .catch { e ->
                    Log.e(TAG, "❌ ERRO ao carregar preferências do usuário: ${e.message}", e)
                    // Usar valores padrão se não conseguir carregar
                    state = state.copy(userName = "Usuário")
                }
                .collect { result ->
                    result.onSuccess { preferences ->
                        Log.d(TAG, "✅ Preferências do usuário carregadas")
                        state = state.copy(userName = preferences.name ?: "Usuário")
                    }
                    result.onFailure { e ->
                        Log.e(TAG, "❌ ERRO ao carregar preferências: ${e.message}", e)
                        state = state.copy(userName = "Usuário")
                    }
                }
        }
    }
    
    /**
     * Carrega dicas personalizadas para o usuário
     */
    private fun loadCustomTips() {
        // Em um cenário real, essas dicas poderiam vir de uma API baseadas no perfil do usuário
        // ou de um algoritmo de recomendação
        state = state.copy(
            customTips = listOf(
                CustomTip(
                    id = "breathing_478",
                    title = "Técnica de Respiração 4-7-8",
                    description = "Uma técnica simples e eficaz para reduzir ansiedade e promover relaxamento."
                ),
                CustomTip(
                    id = "meditation_body_scan",
                    title = "Meditação Body Scan",
                    description = "Uma meditação guiada que ajuda a reconectar com o corpo e liberar tensões."
                )
            )
        )
    }
    
    /**
     * Atualiza a saudação baseada na hora do dia
     */
    private fun updateGreeting() {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        
        Log.d(TAG, "Atualizando saudação para hora: $hourOfDay")
        
        val greeting = when {
            hourOfDay in 5..11 -> "Bom dia"
            hourOfDay in 12..17 -> "Boa tarde"
            else -> "Boa noite"
        }
        
        val greetingEmoji = when {
            hourOfDay in 5..11 -> "☀️"
            hourOfDay in 12..17 -> "🌤️"
            hourOfDay in 18..21 -> "🌆"
            else -> "🌙"
        }
        
        state = state.copy(
            greeting = greeting,
            greetingEmoji = greetingEmoji
        )
    }
    
    /**
     * Método público para atualizar a saudação
     */
    fun refreshGreeting() {
        updateGreeting()
    }
    
    /**
     * Tratar evento de navegação
     */
    fun handleNavigationEvent() {
        state = state.copy(navigationEvent = NavigationEvent.Handled)
    }
    
    private fun loadCheckinData() {
        viewModelScope.launch {
            getLastCheckin()
                .catch { e: Throwable -> 
                    Log.e(TAG, "❌ ERRO ao carregar último check-in: ${e.message}", e)
                    state = state.copy(
                        lastCheckin = "",
                        streakCount = 0
                    )
                }
                .collect { result ->
                    result.onSuccess { checkin: Checkin ->
                        Log.d(TAG, "✅ Último check-in carregado")
                        state = state.copy(
                            lastCheckin = checkin.date,
                            streakCount = checkin.streak ?: 0
                        )
                    }
                    result.onFailure { e ->
                        Log.e(TAG, "❌ ERRO ao carregar último check-in: ${e.message}", e)
                        state = state.copy(
                            lastCheckin = "",
                            streakCount = 0
                        )
                    }
                }
        }
    }
    
    private fun loadFormsData() {
        viewModelScope.launch {
            getPendingForms()
                .catch { e: Throwable ->
                    Log.e(TAG, "❌ ERRO ao carregar formulários pendentes: ${e.message}", e)
                    state = state.copy(
                        pendingForms = 0,
                        isLoading = false
                    )
                }
                .collect { result ->
                    result.onSuccess { forms: List<Form> ->
                        Log.d(TAG, "✅ Formulários pendentes carregados: ${forms.size}")
                        state = state.copy(
                            pendingForms = forms.size,
                            isLoading = false
                        )
                    }
                    result.onFailure { e ->
                        Log.e(TAG, "❌ ERRO ao carregar formulários pendentes: ${e.message}", e)
                        state = state.copy(
                            pendingForms = 0,
                            isLoading = false
                        )
                    }
                }
        }
    }
    
    private fun loadFeelingsData() {
        viewModelScope.launch {
            getFeelings()
                .catch { e: Throwable ->
                    Log.e(TAG, "❌ ERRO ao carregar sentimentos: ${e.message}", e)
                    state = state.copy(feelings = emptyList())
                }
                .collect { result ->
                    result.onSuccess { feelings: List<Feeling> ->
                        Log.d(TAG, "✅ Sentimentos carregados: ${feelings.size}")
                        state = state.copy(feelings = feelings)
                    }
                    result.onFailure { e ->
                        Log.e(TAG, "❌ ERRO ao carregar sentimentos: ${e.message}", e)
                        state = state.copy(feelings = emptyList())
                    }
                }
        }
    }
    
    fun refresh() {
        loadData()
    }
    
    /**
     * Obtém a lista de sentimentos carregados
     */
    fun getFeelingsList(): List<Feeling> {
        return state.feelings
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
        
        Log.d(TAG, "🌐 Tentando enviar feedback para API")
        
        viewModelScope.launch {
            try {
                val reportDTO = ReportDTO(
                    category = state.feedbackCategory,
                    description = state.feedbackDescription,
                    tags = emptyList() // Lista vazia por enquanto, pode ser expandida no futuro
                )
                
                val response = apiService.submit_report(reportDTO)
                Log.d(TAG, "✅ Feedback enviado com sucesso. Location: ${response.location}")
                
                state = state.copy(
                    isSubmittingFeedback = false,
                    feedbackSuccess = true
                )
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ ERRO ao enviar feedback: ${e.message}", e)
                state = state.copy(
                    isSubmittingFeedback = false,
                    feedbackError = "Erro ao enviar feedback: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Enviar check-in com emoji e sentimento
     */
    fun submitCheckin(emoji: String, feeling: String) {
        Log.d(TAG, "🌐 Tentando enviar check-in para API: emoji=$emoji, feeling=$feeling")
        
        viewModelScope.launch {
            try {
                // TODO: Implementar envio real para API
                // Por enquanto apenas registra no log
                Log.w(TAG, "⚠️ Envio de check-in não implementado - dados: emoji=$emoji, feeling=$feeling")
                
                state = state.copy(
                    checkInSuccess = false,
                    checkInError = "Check-in não implementado ainda"
                )
            } catch (e: Exception) {
                Log.e(TAG, "❌ ERRO ao enviar check-in: ${e.message}", e)
                state = state.copy(
                    checkInSuccess = false,
                    checkInError = "Erro ao enviar check-in: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Inicia um questionário específico
     */
    fun startQuestionnaire(code: String) {
        Log.d(TAG, "Iniciando questionário: $code")
        
        when (code) {
            "SELF_ASSESS" -> {
                navController?.let {
                    it.navigate(AppDestinations.formDetail(1))
                } ?: run {
                    state = state.copy(navigationEvent = NavigationEvent.ToForm(1))
                }
            }
            "CLIMATE" -> {
                navController?.let {
                    it.navigate(AppDestinations.formDetail(2))
                } ?: run {
                    state = state.copy(navigationEvent = NavigationEvent.ToForm(2))
                }
            }
            else -> {
                navController?.let {
                    it.navigate(AppDestinations.FORMS)
                } ?: run {
                    state = state.copy(navigationEvent = NavigationEvent.ToForms)
                }
            }
        }
    }
    
    /**
     * Seleciona formulário de relatório
     */
    fun selectReportForm(code: String) {
        Log.d(TAG, "Selecionando formulário de relatório: $code")
        
        if (code == "REPORT") {
            showFeedbackDialog()
        }
    }
    
    /**
     * Mostra um tooltip específico
     */
    fun showTooltip(tooltipId: String) {
        state = state.copy(activeTooltip = tooltipId)
    }
    
    /**
     * Esconde o tooltip atual
     */
    fun hideTooltip() {
        state = state.copy(activeTooltip = null)
    }
    
    /**
     * Navega para a tela de recursos com base na dica selecionada
     */
    fun navigateToResource(resourceId: String) {
        navController?.let {
            it.navigate(AppDestinations.resourceDetail(resourceId))
        } ?: run {
            // Se não temos NavController, emitimos um evento para navegação
            state = state.copy(navigationEvent = NavigationEvent.ToResource(resourceId))
        }
    }
} 