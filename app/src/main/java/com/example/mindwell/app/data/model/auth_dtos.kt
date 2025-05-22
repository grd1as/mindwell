package com.example.mindwell.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * DTO para requisição de login.
 */
data class LoginRequest(
    @SerializedName("id_token")
    val idToken: String
)

/**
 * DTO para resposta de login.
 */
data class LoginResponse(
    @SerializedName("jwt")
    val jwt: String,
    
    @SerializedName("exp")
    val expiresIn: Long
) 