package com.example.mindwell.app.domain.repositories

import com.example.mindwell.app.domain.entities.UserPreferences
import kotlinx.coroutines.flow.Flow

/**
 * Interface para acesso às preferências do usuário.
 */
interface UserPreferencesRepository {
    /**
     * Obtém as preferências do usuário.
     */
    fun getUserPreferences(): Flow<UserPreferences>
    
    /**
     * Atualiza as preferências do usuário.
     */
    suspend fun updateUserPreferences(preferences: UserPreferences): Boolean
}