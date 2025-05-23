package com.example.mindwell.app.presentation.screens.evolution

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindwell.app.domain.entities.Summary
import com.example.mindwell.app.domain.entities.SummaryItem
import com.example.mindwell.app.domain.usecases.summary.GetMonthlySummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val TAG = "EvolutionViewModel"
    
    var state by mutableStateOf(EvolutionState())
        private set
    
    init {
        loadSummary(state.currentMonth)
    }
    
    /**
     * Carrega o resumo para um mês específico
     */
    fun loadSummary(month: YearMonth) {
        state = state.copy(isLoading = true, currentMonth = month)
        
        Log.d(TAG, "🌐 Tentando carregar resumo mensal para ${month.toString()} da API")
        
        getMonthlySummaryUseCase(month)
            .onEach { result ->
                result.fold(
                    onSuccess = { summary ->
                        // Converter dados do resumo para dados de tendência
                        val trendData = extractTrendDataFromSummary(summary)
                        val trendDirection = calculateTrendDirection(trendData)
                        
                        Log.d(TAG, "✅ Sucesso ao carregar resumo mensal da API")
                        
                        state = state.copy(
                            isLoading = false,
                            summary = summary,
                            trendData = trendData,
                            trendDirection = trendDirection,
                            error = null
                        )
                    },
                    onFailure = { exception ->
                        Log.e(TAG, "❌ ERRO ao carregar resumo mensal: ${exception.message}", exception)
                        
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
     * Extrai dados de tendência a partir do resumo.
     * Como a API não fornece dados semanais, essa é uma estimativa baseada nos dados mensais.
     */
    private fun extractTrendDataFromSummary(summary: Summary): List<TrendData> {
        // Essa é uma implementação simples que divide o mês em 4 semanas
        // Em uma implementação real, poderia analisar dados reais por semana
        
        // Se não há dados suficientes, retorna uma lista vazia
        if (summary.total < 4) return emptyList()
        
        // Simplificação: cada semana recebe o nível geral do mês
        return listOf(
            TrendData("Semana 1", summary.overallLevel),
            TrendData("Semana 2", summary.overallLevel),
            TrendData("Semana 3", summary.overallLevel),
            TrendData("Semana 4", summary.overallLevel)
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
        loadSummary(nextMonth)
    }
    
    /**
     * Navega para o mês anterior
     */
    fun previousMonth() {
        val previousMonth = state.currentMonth.minusMonths(1)
        loadSummary(previousMonth)
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