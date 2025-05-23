package com.example.mindwell.app.di

import com.example.mindwell.app.data.network.ApiService
import com.example.mindwell.app.data.network.interceptors.AuthInterceptor
import com.example.mindwell.app.data.network.interceptors.LoggingInterceptorFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    // URL base da API - nova API do Softtek
    private const val BASE_URL = "https://challenge-softtek.onrender.com/"
    private const val TIMEOUT = 30L
    
    @Provides
    @Singleton
    fun provide_okhttp_client(
        auth_interceptor: AuthInterceptor,
        logging_interceptor_factory: LoggingInterceptorFactory
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(auth_interceptor)
            .addInterceptor(logging_interceptor_factory.create(true)) // true para modo debug
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    fun provide_retrofit(okhttp_client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okhttp_client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provide_api_service(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
} 