package com.example.mindwell.app.presentation.screens.settings

import android.util.Log
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
    private val TAG = "SettingsViewModel"
    
    var state by mutableStateOf(SettingsState())
        private set
        
    init {
        loadPreferences()
    }
    
    fun loadPreferences() {
        state = state.copy(isLoading = true)
        
        Log.d(TAG, "🌐 Tentando carregar preferências da API")
        
        getPreferencesUseCase()
            .onEach { result ->
                result.fold(
                    onSuccess = { preferences ->
                        Log.d(TAG, "✅ Sucesso ao carregar preferências da API")
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
                        Log.e(TAG, "❌ ERRO ao carregar preferências: ${exception.message}", exception)
                        state = state.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Erro ao carregar preferências"
                        )
                    }
                )
            }
            .catch { e ->
                Log.e(TAG, "❌ ERRO ao carregar preferências: ${e.message}", e)
                state = state.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro desconhecido"
                )
            }
            .launchIn(viewModelScope)
    }
    
    fun savePreferences() {
        state = state.copy(isSaving = true)
        
        Log.d(TAG, "🌐 Tentando salvar preferências na API")
        
        viewModelScope.launch {
            try {
                // Construindo o objeto Preference a partir do estado atual
                val preference = Preference(
                    notificationsEnabled = state.notificationsEnabled
                )
                
                // Usar o caso de uso real para atualizar as preferências
                updatePreferencesUseCase(preference)
                    .collect { result ->
                        result.fold(
                            onSuccess = { 
                                Log.d(TAG, "✅ Sucesso ao salvar preferências na API")
                                state = state.copy(
                                    isSaving = false,
                                    saveSuccess = true
                                )
                            },
                            onFailure = { exception ->
                                Log.e(TAG, "❌ ERRO ao salvar preferências: ${exception.message}", exception)
                                state = state.copy(
                                    isSaving = false,
                                    errorMessage = exception.message ?: "Erro ao salvar preferências"
                                )
                            }
                        )
                    }
            } catch (e: Exception) {
                Log.e(TAG, "❌ ERRO ao salvar preferências: ${e.message}", e)
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
        Log.d(TAG, "🌐 Realizando logout do usuário")
        viewModelScope.launch {
            logoutUseCase()
                .catch { e ->
                    Log.e(TAG, "❌ ERRO durante logout: ${e.message}", e)
                    // Ignora erros, apenas completa o fluxo
                    onComplete()
                }
                .collect { result ->
                    result.onSuccess {
                        Log.d(TAG, "✅ Logout realizado com sucesso")
                    }
                    result.onFailure { e ->
                        Log.e(TAG, "❌ ERRO durante logout: ${e.message}", e)
                    }
                    // Independente do resultado, completa o fluxo
                    onComplete()
                }
        }
    }
} 