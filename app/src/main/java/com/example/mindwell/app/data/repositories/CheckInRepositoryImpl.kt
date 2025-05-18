package com.example.mindwell.app.data.repositories

import com.example.mindwell.app.data.datasources.local.dao.CheckInDao
import com.example.mindwell.app.data.mappers.CheckInMapper
import com.example.mindwell.app.domain.entities.CheckIn
import com.example.mindwell.app.domain.repositories.CheckInRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do repositório para operações de check-in.
 * Atualmente usando dados em memória para simulação, será substituído por Room.
 */
@Singleton
class CheckInRepositoryImpl @Inject constructor(
    private val checkInDao: CheckInDao
) : CheckInRepository {

    // Armazenamento temporário em memória para simulação
    private val checkIns = mutableListOf<CheckIn>()
    private var nextId = 1L

    override suspend fun saveCheckIn(checkIn: CheckIn): Long {
        val entity = CheckInMapper.toEntity(checkIn)
        return checkInDao.insert(entity)
    }

    override suspend fun getCheckInById(id: Long): CheckIn? {
        val entity = checkInDao.getById(id) ?: return null
        return CheckInMapper.toDomainModel(entity)
    }

    override fun getCheckInsForDate(date: LocalDate): Flow<List<CheckIn>> {
        val startDateTime = LocalDateTime.of(date, LocalTime.MIN)
        val endDateTime = LocalDateTime.of(date, LocalTime.MAX)
        
        return checkInDao.getForDateRange(startDateTime, endDateTime)
            .map { entities ->
                CheckInMapper.toDomainModelList(entities)
            }
    }

    override fun getRecentCheckIns(limit: Int): Flow<List<CheckIn>> {
        return checkInDao.getRecent(limit)
            .map { entities ->
                CheckInMapper.toDomainModelList(entities)
            }
    }

    override suspend fun deleteCheckIn(id: Long): Boolean {
        return checkInDao.delete(id) > 0
    }
    
    override fun getCheckInsForPeriod(startDate: LocalDate, endDate: LocalDate): Flow<List<CheckIn>> {
        val startDateTime = LocalDateTime.of(startDate, LocalTime.MIN)
        val endDateTime = LocalDateTime.of(endDate, LocalTime.MAX)
        
        return checkInDao.getForDateRange(startDateTime, endDateTime)
            .map { entities ->
                CheckInMapper.toDomainModelList(entities)
            }
    }
} 