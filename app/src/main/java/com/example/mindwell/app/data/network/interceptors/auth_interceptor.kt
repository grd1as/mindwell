package com.example.mindwell.app.data.network.interceptors

import android.util.Log
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
        
        Log.d("AuthInterceptor", "🔍 Interceptando requisição: ${originalRequest.url}")
        
        // Ignora rotas de autenticação que não precisam de token
        if (originalRequest.url.encodedPath.contains("auth/mobile")) {
            Log.d("AuthInterceptor", "⏭️ Ignorando rota de auth: ${originalRequest.url}")
            return chain.proceed(originalRequest)
        }
        
        // Obtém o token JWT armazenado
        val token = runBlocking { 
            try {
                tokenStorage.getJwtToken()
            } catch (e: Exception) {
                Log.e("AuthInterceptor", "❌ Erro ao obter token: ${e.message}", e)
                null
            }
        }
        
        Log.d("AuthInterceptor", "🔑 Token obtido: ${if (token != null) "Token presente (${token.take(20)}...)" else "Token ausente"}")
        
        return if (token != null) {
            // Adiciona o token no cabeçalho de autenticação
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            Log.d("AuthInterceptor", "✅ Header Authorization adicionado à requisição")
            Log.d("AuthInterceptor", "🔍 JWT completo enviado: $token")
            chain.proceed(newRequest)
        } else {
            // Se não tiver token, prossegue com a requisição original
            Log.w("AuthInterceptor", "⚠️ Requisição enviada sem token de autenticação")
            chain.proceed(originalRequest)
        }
    }
} 