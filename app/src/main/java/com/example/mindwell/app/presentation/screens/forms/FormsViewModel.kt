package com.example.mindwell.app.presentation.screens.forms

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindwell.app.domain.entities.Form
import com.example.mindwell.app.domain.usecases.form.GetFormsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

/**
 * ViewModel para tela de listagem de formulários.
 */
@HiltViewModel
class FormsViewModel @Inject constructor(
    private val getFormsUseCase: GetFormsUseCase
) : ViewModel() {
    private val TAG = "FormsViewModel"

    // Estado da tela de formulários
    data class FormsState(
        val forms: List<Form> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val filterType: String? = null
    )
    
    // Estado atual da tela
    var state by mutableStateOf(FormsState(isLoading = true))
        private set
    
    // Categorias de formulários
    val formCategories = listOf(
        null to "Todos",
        "daily" to "Diários",
        "weekly" to "Semanais",
        "monthly" to "Mensais"
    )
    
    // Mock data para teste de layout
    private val mockForms = mapOf(
        null to listOf(
            Form(
                id = 1,
                code = "PHQ9",
                name = "Questionário de Saúde do Paciente (PHQ-9)",
                type = "daily",
                description = "Avalia sintomas de depressão nas últimas duas semanas",
                nextAllowed = null,
                lastAnsweredAt = ZonedDateTime.now().minusDays(2)
            ),
            Form(
                id = 2,
                code = "GAD7",
                name = "Transtorno de Ansiedade Generalizada (GAD-7)",
                type = "weekly",
                description = "Avalia sintomas de ansiedade nas últimas duas semanas",
                nextAllowed = null,
                lastAnsweredAt = null
            ),
            Form(
                id = 3,
                code = "PSS10",
                name = "Escala de Estresse Percebido (PSS-10)",
                type = "monthly",
                description = "Avalia o nível de estresse percebido no último mês",
                nextAllowed = ZonedDateTime.now().plusDays(15),
                lastAnsweredAt = ZonedDateTime.now().minusDays(15)
            ),
            Form(
                id = 4,
                code = "SLEEP",
                name = "Questionário de Qualidade do Sono",
                type = "weekly",
                description = "Avalia a qualidade do sono na última semana",
                nextAllowed = null,
                lastAnsweredAt = null
            ),
            Form(
                id = 5,
                code = "MOOD",
                name = "Rastreamento de Humor Diário",
                type = "daily",
                description = "Acompanhamento diário de humor e emoções",
                nextAllowed = null,
                lastAnsweredAt = ZonedDateTime.now().minusDays(1)
            )
        ),
        "daily" to listOf(
            Form(
                id = 1,
                code = "PHQ9",
                name = "Questionário de Saúde do Paciente (PHQ-9)",
                type = "daily",
                description = "Avalia sintomas de depressão nas últimas duas semanas",
                nextAllowed = null,
                lastAnsweredAt = ZonedDateTime.now().minusDays(2)
            ),
            Form(
                id = 5,
                code = "MOOD",
                name = "Rastreamento de Humor Diário",
                type = "daily",
                description = "Acompanhamento diário de humor e emoções",
                nextAllowed = null,
                lastAnsweredAt = ZonedDateTime.now().minusDays(1)
            )
        ),
        "weekly" to listOf(
            Form(
                id = 2,
                code = "GAD7",
                name = "Transtorno de Ansiedade Generalizada (GAD-7)",
                type = "weekly",
                description = "Avalia sintomas de ansiedade nas últimas duas semanas",
                nextAllowed = null,
                lastAnsweredAt = null
            ),
            Form(
                id = 4,
                code = "SLEEP",
                name = "Questionário de Qualidade do Sono",
                type = "weekly",
                description = "Avalia a qualidade do sono na última semana",
                nextAllowed = null,
                lastAnsweredAt = null
            )
        ),
        "monthly" to listOf(
            Form(
                id = 3,
                code = "PSS10",
                name = "Escala de Estresse Percebido (PSS-10)",
                type = "monthly",
                description = "Avalia o nível de estresse percebido no último mês",
                nextAllowed = ZonedDateTime.now().plusDays(15),
                lastAnsweredAt = ZonedDateTime.now().minusDays(15)
            )
        )
    )
    
    init {
        loadForms(useMockData = false) // Usando API real para testes
    }
    
    /**
     * Carrega a lista de formulários.
     * @param useMockData Se true, usa dados mockados para teste de layout
     */
    fun loadForms(useMockData: Boolean = false) {
        state = state.copy(isLoading = true, error = null)
        
        if (useMockData) {
            Log.w(TAG, "📋 USANDO DADOS MOCKADOS para formulários")
            // Use mock data for testing
            viewModelScope.launch {
                // Simulate network delay
                delay(800)
                state = state.copy(
                    forms = mockForms[state.filterType] ?: mockForms[null] ?: emptyList(),
                    isLoading = false
                )
            }
            return
        }
        
        // Real implementation with API
        Log.d(TAG, "🌐 Tentando carregar formulários da API real")
        viewModelScope.launch {
            getFormsUseCase(state.filterType).collect { result ->
                if (result.isSuccess) {
                    val forms = result.getOrNull() ?: emptyList()
                    Log.d(TAG, "✅ Sucesso ao carregar ${forms.size} formulários da API")
                    state = state.copy(
                        forms = forms,
                        isLoading = false
                    )
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Erro ao carregar formulários"
                    Log.e(TAG, "❌ ERRO ao carregar formulários da API: $errorMsg", result.exceptionOrNull())
                    state = state.copy(
                        isLoading = false,
                        error = errorMsg
                    )
                }
            }
        }
    }
    
    /**
     * Filtra formulários por tipo.
     * @param type Tipo de formulário
     */
    fun filterByType(type: String?) {
        if (state.filterType != type) {
            state = state.copy(filterType = type)
            loadForms()
        }
    }
} 