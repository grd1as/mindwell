package com.example.mindwell.app.data.datasources.remote

import com.example.mindwell.app.data.model.PreferenceDTO
import com.example.mindwell.app.data.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fonte de dados remota para preferências.
 */
@Singleton
class PreferenceRemoteDataSource @Inject constructor(
    private val apiService: ApiService
) {
    /**
     * Obtém as preferências do usuário.
     * @return Preferências atuais
     */
    suspend fun getPreferences(): PreferenceDTO {
        return apiService.getPreferences()
    }
    
    /**
     * Atualiza as preferências do usuário.
     * @param preferences Novas preferências
     */
    suspend fun updatePreferences(preferences: PreferenceDTO) {
        apiService.updatePreferences(preferences)
    }
} 