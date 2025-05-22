package com.example.mindwell.app.domain.repositories

import com.example.mindwell.app.domain.entities.User

/**
 * Interface para o repositório de autenticação.
 */
interface AuthRepository {
    /**
     * Realiza login com token do Google.
     * @param idToken Token do Google
     * @return Usuário autenticado com JWT
     */
    suspend fun login(idToken: String): User
    
    /**
     * Verifica se o usuário está autenticado.
     * @return true se estiver autenticado, false caso contrário
     */
    suspend fun isAuthenticated(): Boolean
    
    /**
     * Obtém o token JWT atual.
     * @return Token JWT ou null se não estiver autenticado
     */
    suspend fun getJwtToken(): String?
    
    /**
     * Realiza logout do usuário.
     */
    suspend fun logout()
} 