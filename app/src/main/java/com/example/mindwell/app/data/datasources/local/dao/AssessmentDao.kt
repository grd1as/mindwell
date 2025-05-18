package com.example.mindwell.app.data.datasources.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mindwell.app.data.datasources.local.entities.AssessmentEntity
import com.example.mindwell.app.domain.entities.AssessmentType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * DAO para acesso a dados de avaliações no banco de dados.
 */
@Dao
interface AssessmentDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(assessment: AssessmentEntity): Long
    
    @Query("SELECT * FROM assessments WHERE id = :id")
    suspend fun getById(id: Long): AssessmentEntity?
    
    @Query("SELECT * FROM assessments ORDER BY timestamp DESC")
    fun getAll(): Flow<List<AssessmentEntity>>
    
    @Query("SELECT * FROM assessments WHERE type = :type ORDER BY timestamp DESC")
    fun getByType(type: AssessmentType): Flow<List<AssessmentEntity>>
    
    @Query("SELECT * FROM assessments WHERE type = :type ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestByType(type: AssessmentType): AssessmentEntity?
    
    @Query("SELECT * FROM assessments WHERE timestamp BETWEEN :startDate AND :endDate ORDER BY timestamp DESC")
    fun getForDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<AssessmentEntity>>
    
    @Query("DELETE FROM assessments WHERE id = :id")
    suspend fun delete(id: Long): Int
} 