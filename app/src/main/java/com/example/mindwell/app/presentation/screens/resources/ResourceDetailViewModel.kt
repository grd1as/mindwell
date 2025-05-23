package com.example.mindwell.app.presentation.screens.resources

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindwell.app.domain.entities.ResourceDetail
import com.example.mindwell.app.domain.usecases.resource.GetResourceDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResourceDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getResourceDetailUseCase: GetResourceDetailUseCase
) : ViewModel() {
    private val TAG = "ResourceDetailViewModel"
    
    var state by mutableStateOf(ResourceDetailState())
        private set
    
    fun loadResource(resourceId: String) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            
            try {
                Log.d(TAG, "🌐 Tentando carregar detalhes do recurso $resourceId da API")
                
                getResourceDetailUseCase(resourceId).collect { result ->
                    result.onSuccess { resource ->
                        Log.d(TAG, "✅ Sucesso ao carregar recurso da API")
                        state = state.copy(
                            isLoading = false,
                            resource = resource
                        )
                    }
                    result.onFailure { exception ->
                        Log.e(TAG, "❌ ERRO ao carregar recurso: ${exception.message}", exception)
                        state = state.copy(
                            isLoading = false,
                            error = exception.message ?: "Erro ao carregar o recurso"
                        )
                    }
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ ERRO ao carregar recurso: ${e.message}", e)
                state = state.copy(
                    isLoading = false,
                    error = e.message ?: "Erro ao carregar o recurso"
                )
            }
        }
    }
}

data class ResourceDetailState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val resource: ResourceDetail? = null
) 