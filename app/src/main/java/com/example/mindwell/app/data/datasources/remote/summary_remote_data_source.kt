package com.example.mindwell.app.data.datasources.remote

import com.example.mindwell.app.data.model.SummaryDTO
import com.example.mindwell.app.data.network.ApiService
import java.time.YearMonth
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fonte de dados remota para resumos/consolidações.
 */
@Singleton
class SummaryRemoteDataSource @Inject constructor(
    private val apiService: ApiService
) {
    private val monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM")
    
    /**
     * Obtém o resumo mensal dos check-ins.
     * @param month Mês para o qual obter o resumo
     * @return Resumo com estatísticas consolidadas
     */
    suspend fun getCheckinMonthSummary(month: YearMonth): SummaryDTO {
        val monthString = month.format(monthFormatter)
        return apiService.getSummary(monthString)
    }
    
    /**
     * Obtém o resumo semanal dos check-ins.
     * Converte a semana para o mês correspondente ao primeiro dia da semana.
     * @param year Ano da semana
     * @param weekNumber Número da semana no ano
     * @return Resumo com estatísticas consolidadas
     */
    suspend fun getCheckinWeekSummary(year: Int, weekNumber: Int): SummaryDTO {
        // Obtém o primeiro dia da semana especificada
        val weekFields = WeekFields.of(Locale.getDefault())
        val firstDayOfWeek = LocalDate.ofYearDay(year, 1)
            .with(weekFields.weekOfYear(), weekNumber.toLong())
            .with(weekFields.dayOfWeek(), 1)
        
        // Converte para YearMonth
        val month = YearMonth.of(firstDayOfWeek.year, firstDayOfWeek.month)
        val monthString = month.format(monthFormatter)
        
        // Usa o mês para obter o resumo
        return apiService.getSummary(monthString)
    }
} 