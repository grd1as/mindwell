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
        val error: String? = null,
        val filterType: String? = null
    )
    
    // Estado atual da tela
    var state by mutableStateOf(FormsState(isLoading = true))
        private set
    
    // Categorias de formulários (dados estáticos da UI, não da API)
    val formCategories = listOf(
        null to "Todos",
        "daily" to "Diários",
        "weekly" to "Semanais",
        "monthly" to "Mensais"
    )
    
    init {
        loadForms()
    }
    
    /**
     * Carrega a lista de formulários.
     */
    fun loadForms() {
        state = state.copy(isLoading = true, error = null)
        
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