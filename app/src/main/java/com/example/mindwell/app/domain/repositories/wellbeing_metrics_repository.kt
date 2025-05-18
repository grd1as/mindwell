package com.example.mindwell.app.domain.repositories

import com.example.mindwell.app.domain.entities.WellbeingMetrics
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Interface para acesso a operações de métricas de bem-estar.
 */
interface WellbeingMetricsRepository {
    /**
     * Salva métricas de bem-estar.
     * @return true se as métricas foram salvas com sucesso
     */
    suspend fun saveWellbeingMetrics(metrics: WellbeingMetrics): Boolean
    
    /**
     * Obtém métricas de bem-estar para uma data específica.
     */
    suspend fun getWellbeingMetricsForDate(date: LocalDate): WellbeingMetrics?
    
    /**
     * Obtém métricas de bem-estar para um período específico.
     */
    fun getWellbeingMetricsForPeriod(startDate: LocalDate, endDate: LocalDate): Flow<List<WellbeingMetrics>>
    
    /**
     * Obtém métricas de bem-estar críticas (que indicam problemas).
     */
    fun getCriticalWellbeingMetrics(startDate: LocalDate, endDate: LocalDate): Flow<List<WellbeingMetrics>>
    
    /**
     * Gera as métricas de bem-estar para uma data específica.
     * Isso calculará as métricas com base em check-ins e avaliações disponíveis.
     */
    suspend fun generateWellbeingMetricsForDate(date: LocalDate): WellbeingMetrics?
} 