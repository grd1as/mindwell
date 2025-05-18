package com.example.mindwell.app.domain.usecases.assessment

import com.example.mindwell.app.domain.entities.Assessment
import com.example.mindwell.app.domain.repositories.AssessmentRepository

/**
 * Caso de uso para criar uma nova avaliação psicossocial.
 */
interface CreateAssessmentUseCase {
    /**
     * Cria uma nova avaliação psicossocial.
     * @param assessment A avaliação a ser criada
     * @return ID da avaliação criada
     */
    suspend operator fun invoke(assessment: Assessment): Result<Long>
}

/**
 * Implementação do caso de uso para criar uma nova avaliação psicossocial.
 */
class CreateAssessmentUseCaseImpl(
    private val assessmentRepository: AssessmentRepository
) : CreateAssessmentUseCase {
    /**
     * Cria uma nova avaliação psicossocial.
     * @param assessment A avaliação a ser criada
     * @return ID da avaliação criada
     */
    override suspend operator fun invoke(assessment: Assessment): Result<Long> = runCatching {
        // Validação adicional poderia ser adicionada aqui se necessário
        assessmentRepository.saveAssessment(assessment)
    }
} 