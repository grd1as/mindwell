package com.example.mindwell.app.domain.usecases.wellbeing

import com.example.mindwell.app.domain.entities.WellbeingMetrics
import com.example.mindwell.app.domain.repositories.WellbeingMetricsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

/**
 * Caso de uso para obter métricas de bem-estar críticas que indicam problemas.
 */
class GetCriticalWellbeingMetricsUseCase @Inject constructor(
    private val wellbeingMetricsRepository: WellbeingMetricsRepository
) {
    /**
     * Obtém métricas de bem-estar críticas para um período específico.
     * Por padrão, busca nas últimas 2 semanas.
     * @param startDate Data inicial do período (opcional)
     * @param endDate Data final do período (opcional)
     * @return Flow com as métricas críticas
     */
    operator fun invoke(
        startDate: LocalDate = LocalDate.now().minus(14, ChronoUnit.DAYS),
        endDate: LocalDate = LocalDate.now()
    ): Flow<Result<List<WellbeingMetrics>>> {
        require(!endDate.isBefore(startDate)) { "A data final não pode ser anterior à data inicial" }
        
        return wellbeingMetricsRepository.getCriticalWellbeingMetrics(startDate, endDate)
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
    }
} 