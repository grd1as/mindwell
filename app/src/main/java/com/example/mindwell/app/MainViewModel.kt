package com.example.mindwell.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindwell.app.common.navigation.AppDestinations
import com.example.mindwell.app.domain.usecases.onboarding.GetOnboardingStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel principal do aplicativo.
 * Responsável por determinar a tela inicial com base no estado do onboarding.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val getOnboardingStateUseCase: GetOnboardingStateUseCase
) : ViewModel() {
    
    private val _startDestination = MutableStateFlow(AppDestinations.ONBOARDING)
    val startDestination: StateFlow<String> = _startDestination
    
    init {
        determineStartDestination()
    }
    
    /**
     * Determina qual deve ser a tela inicial baseado no estado do onboarding.
     */
    private fun determineStartDestination() {
        viewModelScope.launch {
            getOnboardingStateUseCase().collectLatest { result ->
                result.getOrNull()?.let { state ->
                    _startDestination.value = if (state.isCompleted) {
                        AppDestinations.LOGIN
                    } else {
                        AppDestinations.ONBOARDING
                    }
                }
            }
        }
    }
} 