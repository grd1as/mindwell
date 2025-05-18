package com.example.mindwell.app.data.repositories

import com.example.mindwell.app.data.datasources.local.dao.CheckInDao
import com.example.mindwell.app.data.datasources.local.dao.WellbeingMetricsDao
import com.example.mindwell.app.data.mappers.CheckInMapper
import com.example.mindwell.app.data.mappers.WellbeingMetricsMapper
import com.example.mindwell.app.domain.entities.WellbeingMetrics
import com.example.mindwell.app.domain.repositories.WellbeingMetricsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Implementação do repositório para operações de métricas de bem-estar.
 */
@Singleton
class WellbeingMetricsRepositoryImpl @Inject constructor(
    private val wellbeingMetricsDao: WellbeingMetricsDao,
    private val checkInDao: CheckInDao
    // private val assessmentDao: AssessmentDao
) : WellbeingMetricsRepository {

    override suspend fun saveWellbeingMetrics(metrics: WellbeingMetrics): Boolean {
        val entity = WellbeingMetricsMapper.toEntity(metrics)
        return wellbeingMetricsDao.insert(entity) >= 0
    }

    override suspend fun getWellbeingMetricsForDate(date: LocalDate): WellbeingMetrics? {
        val entity = wellbeingMetricsDao.getForDate(date) ?: return null
        return WellbeingMetricsMapper.toDomainModel(entity)
    }

    override fun getWellbeingMetricsForPeriod(startDate: LocalDate, endDate: LocalDate): Flow<List<WellbeingMetrics>> {
        return wellbeingMetricsDao.getForDateRange(startDate, endDate)
            .map { entities ->
                WellbeingMetricsMapper.toDomainModelList(entities)
            }
    }

    override fun getCriticalWellbeingMetrics(startDate: LocalDate, endDate: LocalDate): Flow<List<WellbeingMetrics>> {
        return wellbeingMetricsDao.getCriticalMetrics(startDate, endDate)
            .map { entities ->
                WellbeingMetricsMapper.toDomainModelList(entities)
            }
    }

    override suspend fun generateWellbeingMetricsForDate(date: LocalDate): WellbeingMetrics? {
        // Aqui calcularíamos métricas reais baseadas em check-ins e avaliações
        // Por enquanto, vamos gerar dados baseados em check-ins existentes
        
        // Gerar média de humor e estresse baseado em check-ins existentes
        val startDateTime = LocalDateTime.of(date, LocalTime.MIN)
        val endDateTime = LocalDateTime.of(date, LocalTime.MAX)
        
        val checkInsForDate = checkInDao.getForDateRange(startDateTime, endDateTime)
            .map { 
                CheckInMapper.toDomainModelList(it)
            }
            .first()
        
        // Se não houver check-ins, retorna null
        if (checkInsForDate.isEmpty()) {
            return null
        }
        
        val averageMood = checkInsForDate.map { it.moodLevel }.average().toFloat()
        val averageStress = checkInsForDate.map { it.stressLevel }.average().toFloat()
        
        // Gerar pontuações simuladas para workload e environment
        val workloadScore = Random.nextInt(0, 100)
        val environmentScore = Random.nextInt(0, 100)
        
        // Calcular wellbeingScore composto (inversamente proporcional ao estresse)
        val stressComponent = 5 - averageStress // Inverte a escala de estresse (5 = baixo, 1 = alto)
        val moodComponent = averageMood
        val assessmentComponent = (workloadScore + environmentScore) / 2
        
        // Fórmula de bem-estar: 20% estresse + 30% humor + 50% avaliações
        val wellbeingScore = (stressComponent / 5 * 20) + (moodComponent / 5 * 30) + (assessmentComponent / 100 * 50)
        
        val generatedMetrics = WellbeingMetrics(
            date = date,
            averageMood = averageMood,
            averageStress = averageStress,
            workloadScore = workloadScore,
            environmentScore = environmentScore,
            wellbeingScore = wellbeingScore.toFloat()
        )
        
        // Salva as métricas geradas
        saveWellbeingMetrics(generatedMetrics)
        
        return generatedMetrics
    }
} 