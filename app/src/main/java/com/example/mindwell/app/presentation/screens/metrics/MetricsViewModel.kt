package com.example.mindwell.app.presentation.screens.metrics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindwell.app.domain.entities.WellbeingMetrics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Períodos de tempo para análise das métricas
 */
enum class MetricPeriod {
    WEEK, MONTH, QUARTER, YEAR
}

/**
 * Estado da UI para a tela de métricas
 */
data class MetricsUiState(
    val wellbeingMetrics: List<WellbeingMetrics> = emptyList(),
    val selectedPeriod: MetricPeriod = MetricPeriod.WEEK,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * Implementação mock para obter métricas de bem-estar para um período específico.
 */
class MockWellbeingMetricsRepository {
    /**
     * Gera métricas de bem-estar fictícias para um período específico.
     */
    fun getMetricsForPeriod(startDate: LocalDate, endDate: LocalDate): Flow<Result<List<WellbeingMetrics>>> = flow {
        // Simulando um atraso na rede
        kotlinx.coroutines.delay(500)
        
        // Gerar dados fictícios para o período
        val metrics = mutableListOf<WellbeingMetrics>()
        var currentDate = startDate
        
        while (!currentDate.isAfter(endDate)) {
            // Apenas para dias aleatórios para não sobrecarregar a UI
            if (currentDate.dayOfMonth % 2 == 0) {
                metrics.add(
                    WellbeingMetrics(
                        date = currentDate,
                        averageMood = (2.5f + Math.random() * 2.5f).toFloat(),
                        averageStress = (1.5f + Math.random() * 3.5f).toFloat(),
                        workloadScore = (40 + (Math.random() * 40).toInt()),
                        environmentScore = (50 + (Math.random() * 30).toInt()),
                        wellbeingScore = (30f + Math.random() * 60f).toFloat()
                    )
                )
            }
            currentDate = currentDate.plusDays(1)
        }
        
        emit(Result.success(metrics))
    }
}

/**
 * ViewModel para a tela de métricas de bem-estar
 */
class MetricsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MetricsUiState(isLoading = true))
    val uiState: StateFlow<MetricsUiState> = _uiState.asStateFlow()
    
    // Implementação mock do repositório
    private val mockRepository = MockWellbeingMetricsRepository()

    init {
        loadMetrics()
    }

    /**
     * Altera o período selecionado e recarrega as métricas
     */
    fun setSelectedPeriod(period: MetricPeriod) {
        _uiState.update { it.copy(selectedPeriod = period) }
        loadMetrics()
    }

    /**
     * Carrega as métricas para o período selecionado
     */
    private fun loadMetrics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // Determinar datas de início e fim com base no período selecionado
            val today = LocalDate.now()
            val (startDate, endDate) = when (uiState.value.selectedPeriod) {
                MetricPeriod.WEEK -> today.minusWeeks(1) to today
                MetricPeriod.MONTH -> today.minusMonths(1) to today
                MetricPeriod.QUARTER -> today.minusMonths(3) to today
                MetricPeriod.YEAR -> today.minusYears(1) to today
            }
            
            try {
                mockRepository.getMetricsForPeriod(startDate, endDate).collect { result ->
                    result.fold(
                        onSuccess = { metrics ->
                            _uiState.update { 
                                it.copy(
                                    wellbeingMetrics = metrics,
                                    isLoading = false,
                                    error = null
                                )
                            }
                        },
                        onFailure = { error ->
                            _uiState.update { 
                                it.copy(
                                    isLoading = false,
                                    error = error.message ?: "Erro ao carregar métricas de bem-estar"
                                )
                            }
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Erro ao carregar métricas de bem-estar"
                    )
                }
            }
        }
    }
    
    /**
     * Calcula a média de bem-estar para as métricas atuais
     */
    fun calculateAverageWellbeing(): Float {
        return uiState.value.wellbeingMetrics.takeIf { it.isNotEmpty() }
            ?.map { it.wellbeingScore }
            ?.average()
            ?.toFloat() ?: 0f
    }
    
    /**
     * Calcula a média de humor para as métricas atuais
     */
    fun calculateAverageMood(): Float {
        return uiState.value.wellbeingMetrics.takeIf { it.isNotEmpty() }
            ?.map { it.averageMood }
            ?.average()
            ?.toFloat() ?: 0f
    }
    
    /**
     * Calcula a média de estresse para as métricas atuais
     */
    fun calculateAverageStress(): Float {
        return uiState.value.wellbeingMetrics.takeIf { it.isNotEmpty() }
            ?.map { it.averageStress }
            ?.average()
            ?.toFloat() ?: 0f
    }
    
    /**
     * Recarrega as métricas
     */
    fun refreshMetrics() {
        loadMetrics()
    }
} 