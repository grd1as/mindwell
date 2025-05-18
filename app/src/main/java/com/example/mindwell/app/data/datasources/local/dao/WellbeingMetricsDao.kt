package com.example.mindwell.app.data.datasources.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mindwell.app.data.datasources.local.entities.WellbeingMetricsEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * DAO para acesso a dados de métricas de bem-estar no banco de dados.
 */
@Dao
interface WellbeingMetricsDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(metrics: WellbeingMetricsEntity): Long
    
    @Query("SELECT * FROM wellbeing_metrics WHERE date = :date")
    suspend fun getForDate(date: LocalDate): WellbeingMetricsEntity?
    
    @Query("SELECT * FROM wellbeing_metrics WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<WellbeingMetricsEntity>>
    
    @Query("SELECT * FROM wellbeing_metrics WHERE date BETWEEN :startDate AND :endDate AND wellbeingScore < 30 ORDER BY date DESC")
    fun getCriticalMetrics(startDate: LocalDate, endDate: LocalDate): Flow<List<WellbeingMetricsEntity>>
    
    @Query("DELETE FROM wellbeing_metrics WHERE date = :date")
    suspend fun delete(date: LocalDate): Int
} 