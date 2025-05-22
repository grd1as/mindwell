package com.example.mindwell.app.di

import android.content.Context
import com.example.mindwell.app.data.datasources.local.TokenStorage
import com.example.mindwell.app.data.datasources.remote.*
import com.example.mindwell.app.data.network.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    
    @Provides
    @Singleton
    fun provideTokenStorage(
        @ApplicationContext context: Context
    ): TokenStorage {
        return TokenStorage(context)
    }
    
    @Provides
    @Singleton
    fun provideAuthRemoteDataSource(
        apiService: ApiService
    ): AuthRemoteDataSource {
        return AuthRemoteDataSource(apiService)
    }
    
    @Provides
    @Singleton
    fun provideFormRemoteDataSource(
        apiService: ApiService
    ): FormRemoteDataSource {
        return FormRemoteDataSource(apiService)
    }
    
    @Provides
    @Singleton
    fun provideCheckinRemoteDataSource(
        apiService: ApiService
    ): CheckinRemoteDataSource {
        return CheckinRemoteDataSource(apiService)
    }
    
    @Provides
    @Singleton
    fun provideSummaryRemoteDataSource(
        apiService: ApiService
    ): SummaryRemoteDataSource {
        return SummaryRemoteDataSource(apiService)
    }
    
    @Provides
    @Singleton
    fun providePreferenceRemoteDataSource(
        apiService: ApiService
    ): PreferenceRemoteDataSource {
        return PreferenceRemoteDataSource(apiService)
    }
    
    @Provides
    @Singleton
    fun provideReminderRemoteDataSource(
        apiService: ApiService
    ): ReminderRemoteDataSource {
        return ReminderRemoteDataSource(apiService)
    }
    
    @Provides
    @Singleton
    fun provideReportRemoteDataSource(
        apiService: ApiService
    ): ReportRemoteDataSource {
        return ReportRemoteDataSource(apiService)
    }
} 