package com.example.mindwell.app.data.datasources.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mindwell.app.data.datasources.local.entities.FormResponseEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * DAO para acessar as respostas de formulários.
 */
@Dao
interface FormResponseDao {
    @Query("SELECT * FROM form_responses ORDER BY answeredAt DESC")
    fun getAllResponses(): Flow<List<FormResponseEntity>>
    
    @Query("SELECT * FROM form_responses WHERE formType = :formType ORDER BY answeredAt DESC")
    fun getResponsesByType(formType: String): Flow<List<FormResponseEntity>>
    
    @Query("SELECT * FROM form_responses WHERE synced = 0 ORDER BY answeredAt ASC")
    fun getUnsyncedResponses(): Flow<List<FormResponseEntity>>
    
    @Query("SELECT * FROM form_responses WHERE answeredAt BETWEEN :startDate AND :endDate ORDER BY answeredAt DESC")
    fun getResponsesByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<FormResponseEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResponse(response: FormResponseEntity): Long
    
    @Update
    suspend fun updateResponse(response: FormResponseEntity)
    
    @Query("UPDATE form_responses SET synced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: Long)
    
    @Query("DELETE FROM form_responses WHERE id = :id")
    suspend fun deleteResponse(id: Long)
    
    @Query("DELETE FROM form_responses")
    suspend fun deleteAllResponses()
} 