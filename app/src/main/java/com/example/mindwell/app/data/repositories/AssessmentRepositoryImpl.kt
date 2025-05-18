package com.example.mindwell.app.data.repositories

import com.example.mindwell.app.data.datasources.local.dao.AssessmentDao
import com.example.mindwell.app.data.mappers.AssessmentMapper
import com.example.mindwell.app.domain.entities.Assessment
import com.example.mindwell.app.domain.entities.AssessmentType
import com.example.mindwell.app.domain.repositories.AssessmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do repositório para operações de avaliações psicossociais.
 */
@Singleton
class AssessmentRepositoryImpl @Inject constructor(
    private val assessmentDao: AssessmentDao
) : AssessmentRepository {

    override suspend fun saveAssessment(assessment: Assessment): Long {
        val entity = AssessmentMapper.toEntity(assessment)
        return assessmentDao.insert(entity)
    }

    override suspend fun getAssessmentById(id: Long): Assessment? {
        val entity = assessmentDao.getById(id) ?: return null
        return AssessmentMapper.toDomainModel(entity)
    }

    override fun getAllAssessments(): Flow<List<Assessment>> {
        return assessmentDao.getAll()
            .map { entities ->
                AssessmentMapper.toDomainModelList(entities)
            }
    }

    override fun getAssessmentsByType(type: AssessmentType): Flow<List<Assessment>> {
        return assessmentDao.getByType(type)
            .map { entities ->
                AssessmentMapper.toDomainModelList(entities)
            }
    }

    override suspend fun getLatestAssessmentByType(type: AssessmentType): Assessment? {
        val entity = assessmentDao.getLatestByType(type) ?: return null
        return AssessmentMapper.toDomainModel(entity)
    }

    override fun getAssessmentsForPeriod(startDate: LocalDate, endDate: LocalDate): Flow<List<Assessment>> {
        val startDateTime = LocalDateTime.of(startDate, LocalTime.MIN)
        val endDateTime = LocalDateTime.of(endDate, LocalTime.MAX)
        
        return assessmentDao.getForDateRange(startDateTime, endDateTime)
            .map { entities ->
                AssessmentMapper.toDomainModelList(entities)
            }
    }
    
    override suspend fun deleteAssessment(id: Long): Boolean {
        return assessmentDao.delete(id) > 0
    }
} 