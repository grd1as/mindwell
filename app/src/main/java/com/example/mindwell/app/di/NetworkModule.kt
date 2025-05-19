package com.example.mindwell.app.di

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import com.example.mindwell.app.data.network.AuthApi
import com.example.mindwell.app.data.network.JwtInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    @Provides
    @Singleton
    fun okHttp(@ApplicationContext ctx: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(JwtInterceptor(ctx))
            .addInterceptor(HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            })
            .build()
    }


    @Provides @Singleton
    fun retrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

    @Provides @Singleton
    fun authApi(retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)
}
