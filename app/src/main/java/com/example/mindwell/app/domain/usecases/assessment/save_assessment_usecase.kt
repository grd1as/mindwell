package com.example.mindwell.app.domain.usecases.assessment

import com.example.mindwell.app.domain.entities.Assessment
import com.example.mindwell.app.domain.repositories.AssessmentRepository

/**
 * Caso de uso para salvar uma avaliação
 */
interface SaveAssessmentUseCase {
    /**
     * Salva uma avaliação no repositório
     */
    suspend operator fun invoke(assessment: Assessment): Result<Unit>
}

/**
 * Implementação do caso de uso para salvar uma avaliação
 */
class SaveAssessmentUseCaseImpl(
    private val assessmentRepository: AssessmentRepository
) : SaveAssessmentUseCase {
    /**
     * Salva uma avaliação no repositório
     */
    override suspend operator fun invoke(assessment: Assessment): Result<Unit> {
        return try {
            assessmentRepository.saveAssessment(assessment)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 