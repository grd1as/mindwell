package com.example.mindwell.app.data.datasources.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mindwell.app.data.datasources.local.entities.CheckInEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * DAO para acesso a dados de check-ins no banco de dados.
 */
@Dao
interface CheckInDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(checkIn: CheckInEntity): Long
    
    @Query("SELECT * FROM checkins WHERE id = :id")
    suspend fun getById(id: Long): CheckInEntity?
    
    @Query("SELECT * FROM checkins WHERE timestamp BETWEEN :startDate AND :endDate ORDER BY timestamp DESC")
    fun getForDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<CheckInEntity>>
    
    @Query("SELECT * FROM checkins ORDER BY timestamp DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<CheckInEntity>>
    
    @Query("DELETE FROM checkins WHERE id = :id")
    suspend fun delete(id: Long): Int
    
    @Query("SELECT COUNT(*) FROM checkins WHERE timestamp BETWEEN :startDate AND :endDate")
    suspend fun countInDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Int
} 