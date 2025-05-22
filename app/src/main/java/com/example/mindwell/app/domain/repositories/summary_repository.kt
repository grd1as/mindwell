package com.example.mindwell.app.domain.repositories

import com.example.mindwell.app.domain.entities.Summary
import java.time.LocalDate
import java.time.YearMonth

/**
 * Interface para o repositório de resumos/consolidações.
 */
interface SummaryRepository {
    /**
     * Obtém o resumo mensal dos check-ins.
     * @param month Mês para o qual obter o resumo
     * @return Resumo com estatísticas consolidadas
     */
    suspend fun getCheckinMonthSummary(month: YearMonth): Summary
    
    /**
     * Obtém o resumo semanal dos check-ins.
     * @param year Ano da semana
     * @param weekNumber Número da semana no ano
     * @return Resumo com estatísticas consolidadas
     */
    suspend fun getCheckinWeekSummary(year: Int, weekNumber: Int): Summary
} 