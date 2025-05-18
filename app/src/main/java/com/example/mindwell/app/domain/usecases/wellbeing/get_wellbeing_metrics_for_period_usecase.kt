package com.example.mindwell.app.domain.usecases.wellbeing

import com.example.mindwell.app.domain.entities.WellbeingMetrics
import com.example.mindwell.app.domain.repositories.WellbeingMetricsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

/**
 * Caso de uso para obter métricas de bem-estar para um período específico.
 */
class GetWellbeingMetricsForPeriodUseCase @Inject constructor(
    private val wellbeingMetricsRepository: WellbeingMetricsRepository
) {
    /**
     * Obtém métricas de bem-estar para um período específico.
     * @param startDate Data inicial do período
     * @param endDate Data final do período
     * @return Flow com as métricas do período especificado
     */
    operator fun invoke(startDate: LocalDate, endDate: LocalDate): Flow<Result<List<WellbeingMetrics>>> {
        require(!endDate.isBefore(startDate)) { "A data final não pode ser anterior à data inicial" }
        
        return wellbeingMetricsRepository.getWellbeingMetricsForPeriod(startDate, endDate)
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
    }
} 