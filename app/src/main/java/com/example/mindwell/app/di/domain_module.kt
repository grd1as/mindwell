package com.example.mindwell.app.di

import com.example.mindwell.app.domain.usecases.auth.*
import com.example.mindwell.app.domain.usecases.checkin.*
import com.example.mindwell.app.domain.usecases.form.*
import com.example.mindwell.app.domain.usecases.preference.*
import com.example.mindwell.app.domain.usecases.reminder.*
import com.example.mindwell.app.domain.usecases.report.*
import com.example.mindwell.app.domain.usecases.summary.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DomainModule {
    
    // Auth
    @Binds
    @Singleton
    abstract fun bindLoginUseCase(impl: LoginUseCaseImpl): LoginUseCase
    
    @Binds
    @Singleton
    abstract fun bindCheckAuthStatusUseCase(impl: CheckAuthStatusUseCaseImpl): CheckAuthStatusUseCase
    
    @Binds
    @Singleton
    abstract fun bindLogoutUseCase(impl: LogoutUseCaseImpl): LogoutUseCase
    
    // Form
    @Binds
    @Singleton
    abstract fun bindGetFormsUseCase(impl: GetFormsUseCaseImpl): GetFormsUseCase
    
    @Binds
    @Singleton
    abstract fun bindGetFormDetailUseCase(impl: GetFormDetailUseCaseImpl): GetFormDetailUseCase
    
    @Binds
    @Singleton
    abstract fun bindSubmitFormResponsesUseCase(impl: SubmitFormResponsesUseCaseImpl): SubmitFormResponsesUseCase
    
    @Binds
    @Singleton
    abstract fun bindGetPendingFormsUseCase(impl: GetPendingFormsUseCaseImpl): GetPendingFormsUseCase
    
    // Checkin
    @Binds
    @Singleton
    abstract fun bindGetCheckinsUseCase(impl: GetCheckinsUseCaseImpl): GetCheckinsUseCase
    
    @Binds
    @Singleton
    abstract fun bindGetLastCheckinUseCase(impl: GetLastCheckinUseCaseImpl): GetLastCheckinUseCase
    
    // Summary
    @Binds
    @Singleton
    abstract fun bindGetMonthlySummaryUseCase(impl: GetMonthlySummaryUseCaseImpl): GetMonthlySummaryUseCase
    
    @Binds
    @Singleton
    abstract fun bindGetWeeklySummaryUseCase(impl: GetWeeklySummaryUseCaseImpl): GetWeeklySummaryUseCase
    
    // Preference
    @Binds
    @Singleton
    abstract fun bindGetUserPreferencesUseCase(impl: GetUserPreferencesUseCaseImpl): GetUserPreferencesUseCase
    
    @Binds
    @Singleton
    abstract fun bindGetPreferencesUseCase(impl: GetPreferencesUseCaseImpl): GetPreferencesUseCase
    
    @Binds
    @Singleton
    abstract fun bindUpdatePreferencesUseCase(impl: UpdatePreferencesUseCaseImpl): UpdatePreferencesUseCase
    
    // Reminder
    @Binds
    @Singleton
    abstract fun bindGetRemindersUseCase(impl: GetRemindersUseCaseImpl): GetRemindersUseCase
    
    // Report
    @Binds
    @Singleton
    abstract fun bindSubmitReportUseCase(impl: SubmitReportUseCaseImpl): SubmitReportUseCase
} 