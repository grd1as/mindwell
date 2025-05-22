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
    private val api_service: ApiService
) {
    private val month_formatter = DateTimeFormatter.ofPattern("yyyy-MM")
    
    /**
     * Obtém o resumo mensal dos check-ins.
     * @param month Mês para o qual obter o resumo
     * @return Resumo com estatísticas consolidadas
     */
    suspend fun get_checkin_month_summary(month: YearMonth): SummaryDTO {
        val month_string = month.format(month_formatter)
        return api_service.get_summary(month_string)
    }
    
    /**
     * Obtém o resumo semanal dos check-ins.
     * Converte a semana para o mês correspondente ao primeiro dia da semana.
     * @param year Ano da semana
     * @param weekNumber Número da semana no ano
     * @return Resumo com estatísticas consolidadas
     */
    suspend fun get_checkin_week_summary(year: Int, weekNumber: Int): SummaryDTO {
        // Obtém o primeiro dia da semana especificada
        val week_fields = WeekFields.of(Locale.getDefault())
        val first_day_of_week = LocalDate.ofYearDay(year, 1)
            .with(week_fields.weekOfYear(), weekNumber.toLong())
            .with(week_fields.dayOfWeek(), 1)
        
        // Converte para YearMonth
        val month = YearMonth.of(first_day_of_week.year, first_day_of_week.month)
        val month_string = month.format(month_formatter)
        
        // Usa o mês para obter o resumo
        return api_service.get_summary(month_string)
    }
} 