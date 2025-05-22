package com.example.mindwell.app.data.datasources.remote

import com.example.mindwell.app.data.model.SummaryDTO
import com.example.mindwell.app.data.network.ApiService
import java.time.YearMonth
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
        return apiService.getSummary(month = monthString)
    }
    
    /**
     * Obtém o resumo semanal dos check-ins.
     * @param year Ano da semana
     * @param weekNumber Número da semana no ano
     * @return Resumo com estatísticas consolidadas
     */
    suspend fun getCheckinWeekSummary(year: Int, weekNumber: Int): SummaryDTO {
        val weekString = String.format("%04d-W%02d", year, weekNumber)
        return apiService.getSummary(week = weekString)
    }
} 