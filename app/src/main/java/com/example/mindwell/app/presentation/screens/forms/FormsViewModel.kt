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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
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
        val error: String? = null
    )
    
    // Estado atual da tela
    var state by mutableStateOf(FormsState(isLoading = true))
        private set
    
    init {
        loadForms()
    }
    
    /**
     * Carrega a lista de formulários.
     * Exclui checkin (fica só na home) e report (canal de escuta no botão flutuante).
     * Mostra apenas questionários: SELF_ASSESSMENT, CLIMATE, etc.
     */
    fun loadForms() {
        state = state.copy(isLoading = true, error = null)
        
        Log.d(TAG, "🌐 Carregando questionários da API (excluindo checkin e report)")
        viewModelScope.launch {
            getFormsUseCase(null).collect { result ->
                if (result.isSuccess) {
                    val allForms = result.getOrNull() ?: emptyList()
                    
                    // Filtrar apenas questionários válidos (excluir CHECKIN e REPORT)
                    val questionnairesForms = allForms.filter { form ->
                        form.type != "CHECKIN" && form.type != "REPORT"
                    }
                    
                    Log.d(TAG, "✅ Carregados ${allForms.size} formulários, ${questionnairesForms.size} questionários válidos")
                    state = state.copy(
                        forms = questionnairesForms,
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
} 