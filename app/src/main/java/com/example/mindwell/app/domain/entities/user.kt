package com.example.mindwell.app.domain.entities

/**
 * Entidade de domínio representando um usuário autenticado.
 */
data class User(
    val jwt: String,
    val expiresIn: Long
) 