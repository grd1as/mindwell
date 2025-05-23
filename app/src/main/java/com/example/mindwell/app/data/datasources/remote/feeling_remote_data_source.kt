package com.example.mindwell.app.data.datasources.remote

import com.example.mindwell.app.data.model.FeelingDTO
import com.example.mindwell.app.data.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fonte de dados remota para sentimentos.
 */
@Singleton
class FeelingRemoteDataSource @Inject constructor(
    private val api_service: ApiService
) {
    /**
     * Obtém a lista de sentimentos disponíveis para check-in.
     * @return Lista de sentimentos
     */
    suspend fun get_feelings(): List<FeelingDTO> {
        return api_service.get_feelings()
    }
} 