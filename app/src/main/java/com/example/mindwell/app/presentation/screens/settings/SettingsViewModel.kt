package com.example.mindwell.app.presentation.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindwell.app.domain.entities.Preference
import com.example.mindwell.app.domain.usecases.auth.LogoutUseCase
import com.example.mindwell.app.domain.usecases.preference.GetPreferencesUseCase
import com.example.mindwell.app.domain.usecases.preference.UpdatePreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsState(
    val isLoading: Boolean = true,
    val dailyReminder: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false,
    val privacyMode: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getPreferencesUseCase: GetPreferencesUseCase,
    private val updatePreferencesUseCase: UpdatePreferencesUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {
    
    var state by mutableStateOf(SettingsState())
        private set
        
    init {
        // Em um cenário real, carregaríamos do backend
        // Para desenvolvimento, vamos simular dados mockados
        loadMockData()
    }
    
    private fun loadMockData() {
        viewModelScope.launch {
            delay(500) // Simulando carregamento
            state = state.copy(
                isLoading = false,
                dailyReminder = true,
                notificationsEnabled = true,
                darkModeEnabled = false,
                privacyMode = false
            )
        }
    }
    
    fun loadPreferences() {
        state = state.copy(isLoading = true)
        
        getPreferencesUseCase()
            .onEach { result ->
                result.fold(
                    onSuccess = { preferences ->
                        state = state.copy(
                            isLoading = false,
                            notificationsEnabled = preferences.notificationsEnabled,
                            dailyReminder = state.dailyReminder,
                            darkModeEnabled = state.darkModeEnabled,
                            privacyMode = state.privacyMode,
                            errorMessage = null
                        )
                    },
                    onFailure = { exception ->
                        state = state.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Erro ao carregar preferências"
                        )
                    }
                )
            }
            .catch { e ->
                state = state.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro desconhecido"
                )
            }
            .launchIn(viewModelScope)
    }
    
    fun savePreferences() {
        state = state.copy(isSaving = true)
        
        viewModelScope.launch {
            try {
                // Construindo o objeto Preference a partir do estado atual
                val preference = Preference(
                    notificationsEnabled = state.notificationsEnabled
                )
                
                // Simulando um salvamento no backend
                delay(800)
                
                // Em um cenário real, chamaríamos o caso de uso:
                // updatePreferencesUseCase(preference)
                //     .collect { result ->
                //         result.fold(
                //             onSuccess = { ... },
                //             onFailure = { ... }
                //         )
                //     }
                
                state = state.copy(
                    isSaving = false,
                    saveSuccess = true
                )
                
                // Resetar a mensagem de sucesso após 2 segundos
                delay(2000)
                state = state.copy(saveSuccess = false)
            } catch (e: Exception) {
                state = state.copy(
                    isSaving = false,
                    errorMessage = e.message ?: "Erro ao salvar preferências"
                )
            }
        }
    }
    
    fun toggleDailyReminder(enabled: Boolean) {
        state = state.copy(dailyReminder = enabled)
    }
    
    fun toggleNotifications(enabled: Boolean) {
        state = state.copy(notificationsEnabled = enabled)
    }
    
    fun toggleDarkMode(enabled: Boolean) {
        state = state.copy(darkModeEnabled = enabled)
    }
    
    fun togglePrivacyMode(enabled: Boolean) {
        state = state.copy(privacyMode = enabled)
    }
    
    /**
     * Realiza logout do usuário.
     * Esta função utiliza LogoutUseCase para fazer logout completo,
     * incluindo limpar tokens JWT e revogar acesso Google.
     * 
     * @param onComplete Callback chamado após o logout ser concluído
     */
    fun logout(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            logoutUseCase()
                .catch { e ->
                    // Ignora erros, apenas completa o fluxo
                    onComplete()
                }
                .collect { result ->
                    // Independente do resultado, completa o fluxo
                    onComplete()
                }
        }
    }
} 