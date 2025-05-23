package com.example.mindwell.app.data.mappers

import com.example.mindwell.app.data.model.LoginResponse
import com.example.mindwell.app.domain.entities.User

/**
 * Mapper para conversão entre DTO e entidade de usuário.
 */
object UserMapper {
    /**
     * Converte DTO de resposta de login para entidade de domínio User.
     * @param response DTO de resposta de login
     * @return Entidade de domínio User
     */
    fun mapToDomain(response: LoginResponse): User {
        // Como o backend não retorna expiresIn, usamos um padrão de 24 horas
        val defaultExpirationHours = 24L
        val expiresIn = defaultExpirationHours * 3600 // 24 horas em segundos
        
        return User(
            jwt = response.jwt,
            expiresIn = expiresIn
        )
    }
} 