package com.example.mindwell.app.domain.repositories

import com.example.mindwell.app.domain.entities.Assessment
import com.example.mindwell.app.domain.entities.AssessmentType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Interface para acesso a operações de avaliações psicossociais.
 */
interface AssessmentRepository {
    /**
     * Salva uma nova avaliação.
     * @return ID da avaliação salva
     */
    suspend fun saveAssessment(assessment: Assessment): Long
    
    /**
     * Obtém uma avaliação específica pelo ID.
     */
    suspend fun getAssessmentById(id: Long): Assessment?
    
    /**
     * Obtém avaliações de um tipo específico.
     */
    fun getAssessmentsByType(type: AssessmentType): Flow<List<Assessment>>
    
    /**
     * Obtém a avaliação mais recente de um tipo específico.
     */
    suspend fun getLatestAssessmentByType(type: AssessmentType): Assessment?
    
    /**
     * Obtém avaliações para um período específico.
     */
    fun getAssessmentsForPeriod(startDate: LocalDate, endDate: LocalDate): Flow<List<Assessment>>
}