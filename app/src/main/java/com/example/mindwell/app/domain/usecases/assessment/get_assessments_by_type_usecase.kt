package com.example.mindwell.app.domain.usecases.assessment

import com.example.mindwell.app.domain.entities.Assessment
import com.example.mindwell.app.domain.entities.AssessmentType
import com.example.mindwell.app.domain.repositories.AssessmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Caso de uso para obter avaliações por tipo.
 */
class GetAssessmentsByTypeUseCase @Inject constructor(
    private val assessmentRepository: AssessmentRepository
) {
    /**
     * Obtém avaliações de um tipo específico.
     * @param type Tipo de avaliação a ser filtrado
     * @return Flow com as avaliações do tipo especificado
     */
    operator fun invoke(type: AssessmentType): Flow<Result<List<Assessment>>> {
        return assessmentRepository.getAssessmentsByType(type)
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
    }
} 