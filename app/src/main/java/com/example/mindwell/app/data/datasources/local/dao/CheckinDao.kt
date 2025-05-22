package com.example.mindwell.app.data.datasources.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mindwell.app.data.datasources.local.entities.CheckinEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * DAO para acessar os check-ins.
 */
@Dao
interface CheckinDao {
    @Query("SELECT * FROM checkins ORDER BY answeredAt DESC")
    fun getAllCheckins(): Flow<List<CheckinEntity>>
    
    @Query("SELECT * FROM checkins WHERE answeredAt BETWEEN :start_date AND :end_date ORDER BY answeredAt DESC")
    fun getCheckinsByDateRange(start_date: LocalDateTime, end_date: LocalDateTime): Flow<List<CheckinEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckin(checkin: CheckinEntity): Long
    
    @Query("SELECT * FROM checkins ORDER BY answeredAt DESC LIMIT 1")
    fun getLastCheckin(): Flow<CheckinEntity?>
    
    @Query("DELETE FROM checkins")
    suspend fun deleteAllCheckins()
    
    @Query("SELECT COUNT(*) FROM checkins")
    suspend fun getCheckinCount(): Int
} 