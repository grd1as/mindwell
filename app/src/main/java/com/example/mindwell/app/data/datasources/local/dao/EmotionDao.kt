package com.example.mindwell.app.data.datasources.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mindwell.app.data.datasources.local.entities.EmotionEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para acessar as emoções.
 */
@Dao
interface EmotionDao {
    @Query("SELECT * FROM emotions")
    fun getAllEmotions(): Flow<List<EmotionEntity>>
    
    @Query("SELECT * FROM emotions WHERE id = :emotionId")
    suspend fun getEmotionById(emotionId: Long): EmotionEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmotions(emotions: List<EmotionEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmotion(emotion: EmotionEntity): Long
    
    @Query("DELETE FROM emotions")
    suspend fun deleteAllEmotions()
} 