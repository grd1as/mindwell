package com.example.mindwell.app.data.network.interceptors

import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Inject

/**
 * Factory para criar o interceptor de logging HTTP.
 */
class LoggingInterceptorFactory @Inject constructor() {
    /**
     * Cria o interceptor de logging HTTP.
     * @param isDebug Se verdadeiro, configura o nível de log como BODY, caso contrário, NONE
     * @return Interceptor de logging configurado
     */
    fun create(isDebug: Boolean): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (isDebug) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }
} 