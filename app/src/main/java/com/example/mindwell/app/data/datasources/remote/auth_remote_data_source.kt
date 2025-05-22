package com.example.mindwell.app.data.datasources.remote

import com.example.mindwell.app.data.model.LoginRequest
import com.example.mindwell.app.data.model.LoginResponse
import com.example.mindwell.app.data.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fonte de dados remota para autenticação.
 */
@Singleton
class AuthRemoteDataSource @Inject constructor(
    private val apiService: ApiService
) {
    /**
     * Realiza login com token do Google.
     * @param idToken Token do Google
     * @return Resposta com token JWT
     */
    suspend fun login(idToken: String): LoginResponse {
        return apiService.login(LoginRequest(idToken))
    }
} 