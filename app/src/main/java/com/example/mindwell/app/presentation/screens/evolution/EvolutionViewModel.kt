package com.example.mindwell.app.presentation.screens.evolution

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindwell.app.data.model.MonthlyTrendDTO
import com.example.mindwell.app.data.network.ApiService
import com.example.mindwell.app.domain.entities.MonthlySummary
import com.example.mindwell.app.domain.usecases.checkin.GetMonthlySummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

/**
 * Estado da tela de evolução
 */
data class EvolutionState(
    val is_loading: Boolean = true,
    val current_month: YearMonth = YearMonth.now(),
    val monthly_trend: MonthlyTrendDTO? = null,
    val monthly_summary: MonthlySummary? = null,
    val error: String? = null
)

/**
 * ViewModel para a tela de evolução com análise de tendências
 */
@HiltViewModel
class EvolutionViewModel @Inject constructor(
    private val api_service: ApiService,
    private val get_monthly_summary_use_case: GetMonthlySummaryUseCase
) : ViewModel() {
    private val TAG = "EvolutionViewModel"
    
    var state by mutableStateOf(EvolutionState())
        private set
    
    init {
        load_monthly_data(state.current_month)
    }
    
    /**
     * Carrega dados mensais (trend e summary) para um mês específico
     */
    private fun load_monthly_data(month: YearMonth) {
        state = state.copy(is_loading = true, current_month = month)
        
        Log.d(TAG, "🌐 Carregando dados mensais para ${month}")
        
        viewModelScope.launch {
            try {
                // Carrega o resumo mensal usando o use case
                load_monthly_summary(month.year, month.monthValue)
                
                // Carrega análise de tendência (mantendo a API existente)
                load_monthly_trend(month)
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ ERRO ao carregar dados mensais: ${e.message}", e)
                
                state = state.copy(
                    is_loading = false,
                    error = "Erro ao carregar dados: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Carrega o resumo mensal usando o use case
     */
    private suspend fun load_monthly_summary(year: Int, month: Int) {
        get_monthly_summary_use_case(year, month).collectLatest { result ->
            result.fold(
                onSuccess = { summary ->
                    Log.d(TAG, "✅ Resumo mensal carregado com sucesso!")
                    Log.d(TAG, "   - Período: ${summary.period}")
                    Log.d(TAG, "   - Total check-ins: ${summary.total_checkins}")
                    Log.d(TAG, "   - Emoji predominante: ${summary.predominant_emoji.firstOrNull()?.label}")
                    Log.d(TAG, "   - Tendência: ${summary.trend}")
                    
                    state = state.copy(
                        monthly_summary = summary,
                        error = null
                    )
                },
                onFailure = { exception ->
                    Log.e(TAG, "❌ ERRO ao carregar resumo mensal: ${exception.message}", exception)
                    state = state.copy(
                        error = "Erro ao carregar resumo: ${exception.message}"
                    )
                }
            )
        }
    }
    
    /**
     * Carrega análise de tendência para um mês específico
     */
    private suspend fun load_monthly_trend(month: YearMonth) {
        try {
            val monthly_trend = api_service.get_monthly_trend(
                year = month.year,
                month = month.monthValue
            )
            
            Log.d(TAG, "✅ Análise de tendência carregada com sucesso!")
            Log.d(TAG, "   - Período: ${monthly_trend.period}")
            Log.d(TAG, "   - Semanas analisadas: ${monthly_trend.weeklyMood.size}")
            Log.d(TAG, "   - Tendência geral: ${monthly_trend.overallTrend}")
            
            state = state.copy(
                is_loading = false,
                monthly_trend = monthly_trend,
                error = null
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ ERRO ao carregar análise de tendência: ${e.message}", e)
            
            state = state.copy(
                is_loading = false,
                error = "Erro ao carregar análise: ${e.message}"
            )
        }
    }
    
    /**
     * Navega para o próximo mês
     */
    fun next_month() {
        val next_month = state.current_month.plusMonths(1)
        load_monthly_data(next_month)
    }
    
    /**
     * Navega para o mês anterior
     */
    fun previous_month() {
        val previous_month = state.current_month.minusMonths(1)
        load_monthly_data(previous_month)
    }
    
    /**
     * Recarrega os dados do mês atual
     */
    fun refresh() {
        load_monthly_data(state.current_month)
    }
    
    /**
     * Formata o mês atual para exibição
     */
    fun format_current_month(): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
        return state.current_month.format(formatter).replaceFirstChar { 
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 
        }
    }
    
    /**
     * Obtém nomes dos dias da semana
     */
    fun get_weekday_name(weekday: Int): String {
        return when (weekday) {
            0 -> "Dom"
            1 -> "Seg"
            2 -> "Ter"
            3 -> "Qua"
            4 -> "Qui"
            5 -> "Sex"
            6 -> "Sáb"
            else -> "?"
        }
    }
    
    /**
     * Obtém emoji baseado no ID da opção
     */
    fun get_emoji_from_option_id(option_id: Int): String {
        return when (option_id) {
            1 -> "😢" // TRISTE
            2 -> "😊" // ALEGRE
            3 -> "😴" // CANSADO
            4 -> "😰" // ANSIOSO
            5 -> "😨" // MEDO
            6 -> "😡" // RAIVA
            7 -> "😤" // ESTRESSADO
            else -> "😐" // NEUTRO
        }
    }
    
    /**
     * Obtém ícone de tendência baseado no valor
     */
    fun get_trend_icon(): String {
        return when (state.monthly_summary?.trend) {
            "up" -> "📈"
            "down" -> "📉"
            "stable" -> "📊"
            else -> "📊"
        }
    }
    
    /**
     * Obtém dica personalizada com base na tendência
     */
    fun get_trend_tip(): String {
        val trend = state.monthly_trend?.overallTrend ?: ""
        
        return when {
            trend.contains("positiva", ignoreCase = true) || 
            trend.contains("aumento de sentimentos positivos", ignoreCase = true) -> 
                "🌟 Excelente! Continue com suas práticas atuais de bem-estar!"
                
            trend.contains("negativa", ignoreCase = true) || 
            trend.contains("aumento de sentimentos negativos", ignoreCase = true) -> 
                "💙 Considere dedicar mais tempo ao autocuidado e atividades que você gosta."
                
            trend.contains("estável", ignoreCase = true) -> 
                "⚖️ Mantenha o equilíbrio e experimente novas atividades de bem-estar."
                
            else -> "📈 Continue acompanhando sua evolução emocional."
        }
    }
    
    /**
     * Calcula a porcentagem de um valor em relação ao máximo
     */
    fun calculate_percentage(value: Int, max_value: Int): Float {
        return if (max_value > 0) (value.toFloat() / max_value.toFloat()) else 0f
    }
    
    /**
     * Formata a mudança percentual da carga de trabalho
     */
    fun format_workload_change(): String {
        val workload = state.monthly_summary?.workload ?: return "N/A"
        val change = workload.percent_change
        val sign = if (change > 0) "+" else ""
        return "${sign}${String.format("%.1f", change)}%"
    }
    
    /**
     * Obtém cor para a mudança da carga de trabalho
     */
    fun get_workload_change_color(): androidx.compose.ui.graphics.Color {
        val change = state.monthly_summary?.workload?.percent_change ?: 0.0
        return when {
            change > 5 -> androidx.compose.ui.graphics.Color.Red
            change < -5 -> androidx.compose.ui.graphics.Color.Green
            else -> androidx.compose.ui.graphics.Color.Gray
        }
    }
} 