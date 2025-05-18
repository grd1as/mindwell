package com.example.mindwell.app.domain.usecases.wellbeing

import com.example.mindwell.app.domain.entities.WellbeingMetrics
import com.example.mindwell.app.domain.repositories.WellbeingMetricsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate

/**
 * Caso de uso para obter métricas de bem-estar para uma data específica.
 */
interface GetWellbeingMetricsForDateUseCase {
    /**
     * Obtém métricas de bem-estar para uma data específica.
     * @param date Data para a qual obter as métricas
     * @return Flow com o resultado contendo as métricas para a data ou null se não existirem
     */
    operator fun invoke(date: LocalDate): Flow<Result<WellbeingMetrics?>>
}

/**
 * Implementação do caso de uso para obter métricas de bem-estar para uma data específica.
 */
class GetWellbeingMetricsForDateUseCaseImpl(
    private val wellbeingMetricsRepository: WellbeingMetricsRepository
) : GetWellbeingMetricsForDateUseCase {
    /**
     * Obtém métricas de bem-estar para uma data específica.
     * @param date Data para a qual obter as métricas
     * @return Flow com o resultado contendo as métricas para a data ou null se não existirem
     */
    override operator fun invoke(date: LocalDate): Flow<Result<WellbeingMetrics?>> = flow {
        try {
            val metrics = wellbeingMetricsRepository.getWellbeingMetricsForDate(date)
            emit(Result.success(metrics))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 