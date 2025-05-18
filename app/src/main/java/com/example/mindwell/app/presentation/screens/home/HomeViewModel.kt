package com.example.mindwell.app.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindwell.app.domain.entities.CheckIn
import com.example.mindwell.app.domain.entities.Reminder
import com.example.mindwell.app.domain.entities.ReminderPriority
import com.example.mindwell.app.domain.entities.ReminderType
import com.example.mindwell.app.domain.entities.WellbeingMetrics
import com.example.mindwell.app.domain.usecases.checkin.GetRecentCheckInsUseCase
import com.example.mindwell.app.domain.usecases.reminder.GeneratePersonalizedTipsUseCase
import com.example.mindwell.app.domain.usecases.wellbeing.GetWellbeingMetricsForDateUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Estado da UI para a tela inicial
 */
data class HomeUiState(
    val recentCheckIns: List<CheckIn> = emptyList(),
    val todayWellbeing: WellbeingMetrics? = null,
    val personalizedTips: List<Reminder> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val deviceId: String = "" // ID anônimo do dispositivo
)

/**
 * Mock implementation for GetRecentCheckInsUseCase
 */
class MockGetRecentCheckInsUseCase : GetRecentCheckInsUseCase {
    override fun invoke(limit: Int): Flow<Result<List<CheckIn>>> = flow {
        // Create mock data
        val checkIns = listOf(
            CheckIn(
                id = 1L,
                timestamp = LocalDateTime.now().minusDays(1),
                moodLevel = 4,
                stressLevel = 2,
                notes = "Foi um bom dia de trabalho."
            ),
            CheckIn(
                id = 2L,
                timestamp = LocalDateTime.now().minusDays(2),
                moodLevel = 3,
                stressLevel = 3,
                notes = "Dia regular."
            ),
            CheckIn(
                id = 3L,
                timestamp = LocalDateTime.now().minusDays(3),
                moodLevel = 5,
                stressLevel = 1,
                notes = "Dia excelente!"
            )
        )
        emit(Result.success(checkIns.take(limit)))
    }
}

/**
 * Mock implementation for GetWellbeingMetricsForDateUseCase
 */
class MockGetWellbeingMetricsForDateUseCase : GetWellbeingMetricsForDateUseCase {
    override operator fun invoke(date: LocalDate): Flow<Result<WellbeingMetrics?>> = flow {
        val metrics = WellbeingMetrics(
            date = date,
            wellbeingScore = 75.0f,
            averageMood = 4.0f,
            averageStress = 2.5f,
            workloadScore = 65,
            environmentScore = 80
        )
        emit(Result.success(metrics))
    }
}

/**
 * Mock implementation for GeneratePersonalizedTipsUseCase
 */
class MockGeneratePersonalizedTipsUseCase : GeneratePersonalizedTipsUseCase {
    override operator fun invoke(count: Int): Flow<Result<List<Reminder>>> = flow {
        val tips = listOf(
            Reminder(
                id = 1,
                title = "Dica do Dia",
                message = "Faça pausas curtas para melhorar sua produtividade e bem-estar.",
                type = ReminderType.WELLBEING_TIP,
                scheduledTime = LocalDateTime.now(),
                tags = listOf("bem-estar", "produtividade")
            ),
            Reminder(
                id = 2,
                title = "Lembrete de Check-in",
                message = "Não se esqueça de registrar seu estado emocional hoje.",
                type = ReminderType.CHECKIN,
                scheduledTime = LocalDateTime.now(),
                tags = listOf("check-in", "rotina")
            ),
            Reminder(
                id = 3,
                title = "Exercício para hoje",
                message = "Tente fazer 5 minutos de alongamento a cada hora.",
                type = ReminderType.WELLBEING_TIP,
                scheduledTime = LocalDateTime.now(),
                priority = ReminderPriority.HIGH,
                tags = listOf("exercício", "alongamento")
            )
        )
        emit(Result.success(tips.take(count)))
    }
}

/**
 * ViewModel para a tela inicial
 */
class HomeViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    // Mock implementations of the dependencies
    private val getRecentCheckInsUseCase: GetRecentCheckInsUseCase = MockGetRecentCheckInsUseCase()
    private val getWellbeingMetricsForDateUseCase: GetWellbeingMetricsForDateUseCase = MockGetWellbeingMetricsForDateUseCase()
    private val generatePersonalizedTipsUseCase: GeneratePersonalizedTipsUseCase = MockGeneratePersonalizedTipsUseCase()
    
    init {
        loadHomeData()
    }
    
    /**
     * Carrega os dados para a tela inicial
     */
    private fun loadHomeData() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        viewModelScope.launch {
            try {
                // Carrega os check-ins recentes
                loadRecentCheckIns()
                
                // Carrega as métricas de bem-estar para hoje
                loadTodayWellbeing()
                
                // Gera dicas personalizadas
                loadPersonalizedTips()
                
                // Marca como carregado
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Erro ao carregar dados da tela inicial"
                    )
                }
            }
        }
    }
    
    /**
     * Carrega os check-ins recentes
     */
    private suspend fun loadRecentCheckIns() {
        try {
            getRecentCheckInsUseCase(5).collect { result ->
                result.fold(
                    onSuccess = { checkIns ->
                        _uiState.update { it.copy(recentCheckIns = checkIns) }
                    },
                    onFailure = { error ->
                        _uiState.update { 
                            it.copy(error = "Erro ao carregar check-ins: ${error.message}")
                        }
                    }
                )
            }
        } catch (e: Exception) {
            // Apenas registre o erro, mas não interrompa o carregamento de outros dados
            _uiState.update { 
                it.copy(error = "Erro ao carregar check-ins: ${e.message}")
            }
        }
    }
    
    /**
     * Carrega as métricas de bem-estar para hoje
     */
    private suspend fun loadTodayWellbeing() {
        try {
            val today = LocalDate.now()
            getWellbeingMetricsForDateUseCase(today).collect { result ->
                result.fold(
                    onSuccess = { metrics ->
                        _uiState.update { it.copy(todayWellbeing = metrics) }
                    },
                    onFailure = { error ->
                        // Ignora erro, pois pode não haver métricas para hoje ainda
                    }
                )
            }
        } catch (e: Exception) {
            // Apenas registre o erro, mas não interrompa o carregamento de outros dados
        }
    }
    
    /**
     * Gera dicas personalizadas
     */
    private suspend fun loadPersonalizedTips() {
        try {
            generatePersonalizedTipsUseCase(3).collect { result ->
                result.fold(
                    onSuccess = { tips ->
                        _uiState.update { it.copy(personalizedTips = tips) }
                    },
                    onFailure = { error ->
                        // Se falhar, usa algumas dicas padrão
                        _uiState.update { 
                            it.copy(personalizedTips = getDefaultTips())
                        }
                    }
                )
            }
        } catch (e: Exception) {
            // Se falhar, usa algumas dicas padrão
            _uiState.update { it.copy(personalizedTips = getDefaultTips()) }
        }
    }
    
    /**
     * Retorna algumas dicas padrão
     */
    private fun getDefaultTips(): List<Reminder> {
        return listOf(
            Reminder(
                id = 1,
                title = "Dica do Dia",
                message = "Faça pausas curtas para melhorar sua produtividade e bem-estar.",
                type = ReminderType.WELLBEING_TIP,
                scheduledTime = LocalDateTime.now(),
                tags = listOf("bem-estar", "produtividade")
            ),
            Reminder(
                id = 2,
                title = "Lembrete de Check-in",
                message = "Não se esqueça de registrar seu estado emocional hoje.",
                type = ReminderType.CHECKIN,
                scheduledTime = LocalDateTime.now(),
                tags = listOf("check-in", "rotina")
            )
        )
    }
    
    /**
     * Recarrega os dados da tela inicial
     */
    fun refreshHomeData() {
        loadHomeData()
    }
} 