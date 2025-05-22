package com.example.mindwell.app.domain.repositories

import com.example.mindwell.app.domain.entities.User

/**
 * Interface para o repositório de autenticação.
 */
interface AuthRepository {
    /**
     * Realiza login com token do Google.
     * @param idToken Token de ID do Google
     * @return Usuário autenticado ou null em caso de falha
     */
    suspend fun login(idToken: String): User?
    
    /**
     * Realiza logout.
     * @return true se o logout foi bem-sucedido
     */
    suspend fun logout(): Boolean
    
    /**
     * Verifica se o usuário está autenticado.
     * @return true se o usuário estiver autenticado e o token for válido
     */
    fun isAuthenticated(): Boolean
    
    /**
     * Obtém o token JWT.
     * @return Token JWT ou null se não estiver autenticado
     */
    fun getJwtToken(): String?
} 