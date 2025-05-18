package com.example.mindwell.app.presentation.screens.checkin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindwell.app.domain.usecases.checkin.CreateCheckInUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

/**
 * Estado da UI para a tela de check-in
 */
data class CheckInUiState(
    val mood: Int = 3,
    val stressLevel: Int = 3,
    val notes: String = "",
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

/**
 * Implementação mock do caso de uso para criar um check-in
 */
class MockCreateCheckInUseCase : CreateCheckInUseCase {
    override suspend fun invoke(moodLevel: Int, stressLevel: Int, notes: String?): Result<Long> {
        // Simulate some delay
        kotlinx.coroutines.delay(500)
        return Result.success(1L)
    }
}

/**
 * ViewModel para a tela de check-in
 */
class CheckInViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(CheckInUiState())
    val uiState: StateFlow<CheckInUiState> = _uiState.asStateFlow()
    
    // Mock implementation for CreateCheckInUseCase
    private val createCheckInUseCase: CreateCheckInUseCase = MockCreateCheckInUseCase()
    
    /**
     * Atualiza o humor selecionado
     */
    fun updateMood(mood: Int) {
        _uiState.value = _uiState.value.copy(mood = mood)
    }
    
    /**
     * Atualiza o nível de estresse
     */
    fun updateStressLevel(level: Int) {
        _uiState.value = _uiState.value.copy(stressLevel = level)
    }
    
    /**
     * Atualiza as anotações
     */
    fun updateNotes(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }
    
    /**
     * Salva o check-in atual
     */
    fun saveCheckIn() {
        val currentState = _uiState.value
        
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                val result = createCheckInUseCase(
                    moodLevel = currentState.mood,
                    stressLevel = currentState.stressLevel,
                    notes = currentState.notes.takeIf { it.isNotBlank() }
                )
                
                result.fold(
                    onSuccess = { id ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSaved = true
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = error.message ?: "Erro ao salvar check-in"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Erro ao salvar check-in"
                )
            }
        }
    }
    
    /**
     * Reseta o estado após navegação
     */
    fun resetSaveState() {
        _uiState.value = _uiState.value.copy(isSaved = false, error = null)
    }
} 