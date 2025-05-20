package com.example.mindwell.app.data.network

import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val id_token: String)
data class LoginResponse(val jwt: String)

interface AuthApi {
    @POST("auth/mobile")
    suspend fun login(@Body body: LoginRequest): LoginResponse
}
