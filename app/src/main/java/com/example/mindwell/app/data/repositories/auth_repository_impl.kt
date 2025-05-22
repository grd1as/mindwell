package com.example.mindwell.app.data.repositories

import com.example.mindwell.app.data.datasources.local.TokenStorage
import com.example.mindwell.app.data.datasources.remote.AuthRemoteDataSource
import com.example.mindwell.app.data.mappers.UserMapper
import com.example.mindwell.app.domain.entities.User
import com.example.mindwell.app.domain.repositories.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do repositório de autenticação.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: AuthRemoteDataSource,
    private val tokenStorage: TokenStorage
) : AuthRepository {
    /**
     * Realiza login com token do Google.
     * @param idToken Token do Google
     * @return Usuário autenticado com JWT
     */
    override suspend fun login(idToken: String): User {
        val response = remoteDataSource.login(idToken)
        val user = UserMapper.mapToDomain(response)
        
        // Armazena o token para uso futuro
        tokenStorage.saveJwtToken(user.jwt, user.expiresIn)
        
        return user
    }
    
    /**
     * Verifica se o usuário está autenticado.
     * @return true se estiver autenticado, false caso contrário
     */
    override suspend fun isAuthenticated(): Boolean {
        return tokenStorage.hasValidToken()
    }
    
    /**
     * Obtém o token JWT atual.
     * @return Token JWT ou null se não estiver autenticado
     */
    override suspend fun getJwtToken(): String? {
        return tokenStorage.getJwtToken()
    }
    
    /**
     * Realiza logout do usuário.
     */
    override suspend fun logout() {
        tokenStorage.clearToken()
    }
} 