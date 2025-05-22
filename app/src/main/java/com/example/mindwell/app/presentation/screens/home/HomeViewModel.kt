package com.example.mindwell.app.presentation.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val getPendingForms: GetPendingFormsUseCase
) : ViewModel() {
    // Estado da tela home
    data class HomeState(
        val isLoading: Boolean = true,
        val userName: String = "",
        val lastCheckin: String = "",
        val pendingForms: Int = 0,
        val streakCount: Int = 0,
        val error: String? = null
    )
    
    // Estado atual da tela
    var state by mutableStateOf(HomeState())
        private set
    
    init {
        loadData()
    }
    
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
} 