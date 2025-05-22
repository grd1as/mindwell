package com.example.mindwell.app.presentation.screens.evolution

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindwell.app.domain.entities.Summary
import com.example.mindwell.app.domain.entities.SummaryItem
import com.example.mindwell.app.domain.usecases.summary.GetMonthlySummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * Dados de tendência para a visualização de evolução
 */
data class TrendData(
    val weekLabel: String,
    val moodLevel: String
)

/**
 * Estado da tela de resultados/evolução
 */
data class EvolutionState(
    val isLoading: Boolean = true,
    val currentMonth: YearMonth = YearMonth.now(),
    val summary: Summary? = null,
    val trendData: List<TrendData> = emptyList(),
    val trendDirection: String = "",
    val error: String? = null
)

/**
 * ViewModel para a tela de resultados/evolução
 */
@HiltViewModel
class EvolutionViewModel @Inject constructor(
    private val getMonthlySummaryUseCase: GetMonthlySummaryUseCase
) : ViewModel() {
    
    var state by mutableStateOf(EvolutionState())
        private set
    
    init {
        loadMockedData(state.currentMonth)
    }
    
    /**
     * Carrega dados mockados para a tela
     */
    fun loadMockedData(month: YearMonth) {
        state = state.copy(isLoading = true, currentMonth = month)
        
        // Simular um breve carregamento para UX
        viewModelScope.launch {
            delay(500) // Delay artificial para simular carregamento
            
            // Mockando dados do resumo
            val mockSummary = Summary(
                total = 28,
                breakdown = listOf(
                    SummaryItem("Muito Bom", 8, 29, "VERY_GOOD"),
                    SummaryItem("Bom", 12, 43, "GOOD"),
                    SummaryItem("Neutro", 5, 18, "NEUTRAL"),
                    SummaryItem("Ruim", 2, 7, "BAD"),
                    SummaryItem("Muito Ruim", 1, 3, "VERY_BAD")
                ),
                overallLevel = "GOOD"
            )
            
            // Mockando dados de tendência
            val trendData = getMockedTrendData()
            val trendDirection = calculateTrendDirection(trendData)
            
            // Atualizar estado
            state = state.copy(
                isLoading = false,
                summary = mockSummary,
                trendData = trendData,
                trendDirection = trendDirection,
                error = null
            )
        }
    }
    
    /**
     * Carrega o resumo para um mês específico - USANDO API (desativado temporariamente)
     */
    private fun loadSummaryFromApi(month: YearMonth) {
        state = state.copy(isLoading = true, currentMonth = month)
        
        getMonthlySummaryUseCase(month)
            .onEach { result ->
                result.fold(
                    onSuccess = { summary ->
                        val trendData = getMockedTrendData()
                        val trendDirection = calculateTrendDirection(trendData)
                        
                        state = state.copy(
                            isLoading = false,
                            summary = summary,
                            trendData = trendData,
                            trendDirection = trendDirection,
                            error = null
                        )
                    },
                    onFailure = { exception ->
                        state = state.copy(
                            isLoading = false,
                            error = exception.message ?: "Erro ao carregar o resumo"
                        )
                    }
                )
            }
            .launchIn(viewModelScope)
    }
    
    /**
     * Gera dados mockados de tendência
     */
    private fun getMockedTrendData(): List<TrendData> {
        return listOf(
            TrendData("Semana 1", "NEUTRAL"),
            TrendData("Semana 2", "BAD"),
            TrendData("Semana 3", "NEUTRAL"),
            TrendData("Semana 4", "GOOD")
        )
    }
    
    /**
     * Calcula a direção da tendência com base nos dados de humor
     */
    private fun calculateTrendDirection(trendData: List<TrendData>): String {
        if (trendData.isEmpty()) return "estável"
        
        val lastLevel = trendData.last().moodLevel
        return when {
            lastLevel == "GOOD" || lastLevel == "VERY_GOOD" -> "positiva"
            lastLevel == "BAD" || lastLevel == "VERY_BAD" -> "negativa"
            else -> "estável"
        }
    }
    
    /**
     * Obtém dica personalizada com base na tendência
     */
    fun getTrendTip(): String {
        return when (state.trendDirection) {
            "positiva" -> "Continue com suas práticas atuais de bem-estar!"
            "negativa" -> "Considere dedicar mais tempo ao autocuidado e atividades que você gosta."
            else -> "Mantenha o equilíbrio e experimente novas atividades de bem-estar."
        }
    }
    
    /**
     * Navega para o próximo mês
     */
    fun nextMonth() {
        val nextMonth = state.currentMonth.plusMonths(1)
        loadMockedData(nextMonth)
    }
    
    /**
     * Navega para o mês anterior
     */
    fun previousMonth() {
        val previousMonth = state.currentMonth.minusMonths(1)
        loadMockedData(previousMonth)
    }
    
    /**
     * Formata o mês atual para exibição
     */
    fun formatCurrentMonth(): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return state.currentMonth.format(formatter).capitalize()
    }
    
    /**
     * Extensão para capitalizar a primeira letra de uma string
     */
    private fun String.capitalize(): String {
        return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
} 