package com.example.mindwell.app.data.network.interceptors

import com.example.mindwell.app.data.datasources.local.TokenStorage
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Interceptor para adicionar token JWT nos cabeçalhos das requisições.
 */
class AuthInterceptor @Inject constructor(
    private val tokenStorage: TokenStorage
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Ignora rotas de autenticação que não precisam de token
        if (originalRequest.url.encodedPath.contains("auth/mobile")) {
            return chain.proceed(originalRequest)
        }
        
        // Obtém o token JWT armazenado
        val token = runBlocking { tokenStorage.getJwtToken() }
        
        return if (token != null) {
            // Adiciona o token no cabeçalho de autenticação
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            chain.proceed(newRequest)
        } else {
            // Se não tiver token, prossegue com a requisição original
            chain.proceed(originalRequest)
        }
    }
} 