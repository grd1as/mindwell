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
    private val api_service: ApiService
) {
    /**
     * Obtém as preferências do usuário.
     * @return Preferências atuais
     */
    suspend fun get_preferences(): PreferenceDTO {
        return api_service.get_preferences()
    }
    
    /**
     * Atualiza as preferências do usuário.
     * @param preferences Novas preferências
     */
    suspend fun update_preferences(preferences: PreferenceDTO) {
        api_service.update_preferences(preferences)
    }
} 