package com.example.mindwell.app.data.network

import com.example.mindwell.app.data.model.LoginRequest
import com.example.mindwell.app.data.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/mobile")
    suspend fun login(@Body body: LoginRequest): LoginResponse
}
