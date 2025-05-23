package com.example.mindwell.app.presentation.screens.evolution

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindwell.app.data.model.MonthlyTrendDTO
import com.example.mindwell.app.data.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

/**
 * Estado da tela de evolução
 */
data class EvolutionState(
    val isLoading: Boolean = true,
    val currentMonth: YearMonth = YearMonth.now(),
    val monthlyTrend: MonthlyTrendDTO? = null,
    val error: String? = null
)

/**
 * ViewModel para a tela de evolução com análise de tendências
 */
@HiltViewModel
class EvolutionViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {
    private val TAG = "EvolutionViewModel"
    
    var state by mutableStateOf(EvolutionState())
        private set
    
    init {
        loadMonthlyTrend(state.currentMonth)
    }
    
    /**
     * Carrega análise de tendência para um mês específico
     */
    private fun loadMonthlyTrend(month: YearMonth) {
        state = state.copy(isLoading = true, currentMonth = month)
        
        Log.d(TAG, "🌐 Tentando carregar análise de tendência mensal para ${month}")
        
        viewModelScope.launch {
            try {
                val monthlyTrend = apiService.get_monthly_trend(
                    year = month.year,
                    month = month.monthValue
                )
                
                Log.d(TAG, "✅ Análise de tendência carregada com sucesso!")
                Log.d(TAG, "   - Período: ${monthlyTrend.period}")
                Log.d(TAG, "   - Semanas analisadas: ${monthlyTrend.weeklyMood.size}")
                Log.d(TAG, "   - Tendência geral: ${monthlyTrend.overallTrend}")
                
                state = state.copy(
                    isLoading = false,
                    monthlyTrend = monthlyTrend,
                    error = null
                )
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ ERRO ao carregar análise de tendência: ${e.message}", e)
                
                state = state.copy(
                    isLoading = false,
                    error = "Erro ao carregar análise: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Navega para o próximo mês
     */
    fun nextMonth() {
        val nextMonth = state.currentMonth.plusMonths(1)
        loadMonthlyTrend(nextMonth)
    }
    
    /**
     * Navega para o mês anterior
     */
    fun previousMonth() {
        val previousMonth = state.currentMonth.minusMonths(1)
        loadMonthlyTrend(previousMonth)
    }
    
    /**
     * Recarrega os dados do mês atual
     */
    fun refresh() {
        loadMonthlyTrend(state.currentMonth)
    }
    
    /**
     * Formata o mês atual para exibição
     */
    fun formatCurrentMonth(): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
        return state.currentMonth.format(formatter).replaceFirstChar { 
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 
        }
    }
    
    /**
     * Obtém nomes dos dias da semana
     */
    fun getWeekdayName(weekday: Int): String {
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
    fun getEmojiFromOptionId(optionId: Int): String {
        return when (optionId) {
            1 -> "😢" // TRISTE
            2 -> "😊" // ALEGRE
            3 -> "😴" // CANSADO
            4 -> "😰" // ANSIOSO
            5 -> "😨" // MEDO
            6 -> "😡" // RAIVA
            else -> "😐" // NEUTRO
        }
    }
    
    /**
     * Obtém dica personalizada com base na tendência
     */
    fun getTrendTip(): String {
        val trend = state.monthlyTrend?.overallTrend ?: ""
        
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
    fun calculatePercentage(value: Int, maxValue: Int): Float {
        return if (maxValue > 0) (value.toFloat() / maxValue.toFloat()) else 0f
    }
} 