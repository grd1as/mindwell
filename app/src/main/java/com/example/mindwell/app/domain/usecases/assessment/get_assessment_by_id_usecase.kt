package com.example.mindwell.app.domain.usecases.assessment

import com.example.mindwell.app.domain.entities.Assessment
import com.example.mindwell.app.domain.repositories.AssessmentRepository
import javax.inject.Inject

/**
 * Caso de uso para obter uma avaliação específica pelo ID.
 */
class GetAssessmentByIdUseCase @Inject constructor(
    private val assessmentRepository: AssessmentRepository
) {
    /**
     * Obtém uma avaliação pelo ID.
     * @param id ID da avaliação a ser obtida
     * @return A avaliação encontrada ou null se não existir
     */
    suspend operator fun invoke(id: Long): Result<Assessment?> = runCatching {
        assessmentRepository.getAssessmentById(id)
    }
} 