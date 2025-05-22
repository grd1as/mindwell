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
    fun provide_token_storage(
        @ApplicationContext context: Context
    ): TokenStorage {
        return TokenStorage(context)
    }
    
    @Provides
    @Singleton
    fun provide_auth_remote_data_source(
        api_service: ApiService
    ): AuthRemoteDataSource {
        return AuthRemoteDataSource(api_service)
    }
    
    @Provides
    @Singleton
    fun provide_form_remote_data_source(
        api_service: ApiService
    ): FormRemoteDataSource {
        return FormRemoteDataSource(api_service)
    }
    
    @Provides
    @Singleton
    fun provide_checkin_remote_data_source(
        api_service: ApiService
    ): CheckinRemoteDataSource {
        return CheckinRemoteDataSource(api_service)
    }
    
    @Provides
    @Singleton
    fun provide_summary_remote_data_source(
        api_service: ApiService
    ): SummaryRemoteDataSource {
        return SummaryRemoteDataSource(api_service)
    }
    
    @Provides
    @Singleton
    fun provide_preference_remote_data_source(
        api_service: ApiService
    ): PreferenceRemoteDataSource {
        return PreferenceRemoteDataSource(api_service)
    }
    
    @Provides
    @Singleton
    fun provide_reminder_remote_data_source(
        api_service: ApiService
    ): ReminderRemoteDataSource {
        return ReminderRemoteDataSource(api_service)
    }
    
    @Provides
    @Singleton
    fun provide_report_remote_data_source(
        api_service: ApiService
    ): ReportRemoteDataSource {
        return ReportRemoteDataSource(api_service)
    }
} 