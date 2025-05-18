package com.example.mindwell.app.domain.usecases.assessment

import com.example.mindwell.app.domain.entities.Assessment
import com.example.mindwell.app.domain.entities.AssessmentType
import com.example.mindwell.app.domain.repositories.AssessmentRepository
import javax.inject.Inject

/**
 * Caso de uso para obter a avaliação mais recente de um tipo específico.
 */
class GetLatestAssessmentByTypeUseCase @Inject constructor(
    private val assessmentRepository: AssessmentRepository
) {
    /**
     * Obtém a avaliação mais recente de um tipo específico.
     * @param type Tipo de avaliação a ser buscado
     * @return A avaliação mais recente do tipo especificado ou null se não houver nenhuma
     */
    suspend operator fun invoke(type: AssessmentType): Result<Assessment?> = runCatching {
        assessmentRepository.getLatestAssessmentByType(type)
    }
} 