package com.example.mindwell.app.presentation.screens.metrics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindwell.app.domain.entities.WellbeingMetrics
import com.example.mindwell.app.domain.usecases.wellbeing.GetWellbeingMetricsForPeriodUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

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
 * ViewModel para a tela de métricas de bem-estar
 */
@HiltViewModel
class MetricsViewModel @Inject constructor(
    private val getWellbeingMetricsForPeriodUseCase: GetWellbeingMetricsForPeriodUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MetricsUiState(isLoading = true))
    val uiState: StateFlow<MetricsUiState> = _uiState.asStateFlow()

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
            
            getWellbeingMetricsForPeriodUseCase(startDate, endDate).collect { result ->
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