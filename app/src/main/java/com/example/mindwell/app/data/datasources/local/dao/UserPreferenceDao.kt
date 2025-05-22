package com.example.mindwell.app.data.datasources.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mindwell.app.data.datasources.local.entities.UserPreferenceEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para acessar as preferências do usuário.
 */
@Dao
interface UserPreferenceDao {
    @Query("SELECT * FROM user_preferences LIMIT 1")
    fun getUserPreferences(): Flow<UserPreferenceEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserPreference(preference: UserPreferenceEntity)
    
    @Update
    suspend fun updateUserPreference(preference: UserPreferenceEntity)
    
    @Query("DELETE FROM user_preferences")
    suspend fun deleteAllPreferences()
} 