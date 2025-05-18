package com.example.mindwell.app.domain.repositories

import com.example.mindwell.app.domain.entities.Assessment
import com.example.mindwell.app.domain.entities.AssessmentType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Interface para operações no repositório de avaliações
 */
interface AssessmentRepository {
    /**
     * Salva uma avaliação
     * @return id da avaliação salva
     */
    suspend fun saveAssessment(assessment: Assessment): Long
    
    /**
     * Obtém uma avaliação pelo id
     */
    suspend fun getAssessmentById(id: Long): Assessment?
    
    /**
     * Obtém todas as avaliações
     */
    fun getAllAssessments(): Flow<List<Assessment>>
    
    /**
     * Obtém avaliações por tipo
     */
    fun getAssessmentsByType(type: AssessmentType): Flow<List<Assessment>>
    
    /**
     * Obtém avaliações para um período específico
     */
    fun getAssessmentsForPeriod(startDate: LocalDate, endDate: LocalDate): Flow<List<Assessment>>
    
    /**
     * Obtém a avaliação mais recente por tipo
     */
    suspend fun getLatestAssessmentByType(type: AssessmentType): Assessment?
    
    /**
     * Exclui uma avaliação
     */
    suspend fun deleteAssessment(id: Long): Boolean
}