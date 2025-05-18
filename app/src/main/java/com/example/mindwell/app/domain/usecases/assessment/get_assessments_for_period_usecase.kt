package com.example.mindwell.app.domain.usecases.assessment

import com.example.mindwell.app.domain.entities.Assessment
import com.example.mindwell.app.domain.repositories.AssessmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

/**
 * Caso de uso para obter avaliações para um período específico.
 */
class GetAssessmentsForPeriodUseCase @Inject constructor(
    private val assessmentRepository: AssessmentRepository
) {
    /**
     * Obtém avaliações para um período específico.
     * @param startDate Data inicial do período
     * @param endDate Data final do período
     * @return Flow com as avaliações do período especificado
     */
    operator fun invoke(startDate: LocalDate, endDate: LocalDate): Flow<Result<List<Assessment>>> {
        require(!endDate.isBefore(startDate)) { "A data final não pode ser anterior à data inicial" }
        
        return assessmentRepository.getAssessmentsForPeriod(startDate, endDate)
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
    }
} 