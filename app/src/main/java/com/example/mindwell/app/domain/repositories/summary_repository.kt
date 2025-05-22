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
    suspend fun get_checkin_month_summary(month: YearMonth): Summary
    
    /**
     * Obtém o resumo semanal dos check-ins.
     * @param year Ano da semana
     * @param week_number Número da semana no ano
     * @return Resumo com estatísticas consolidadas
     */
    suspend fun get_checkin_week_summary(year: Int, week_number: Int): Summary
} 