package com.example.mindwell.app.di

import com.example.mindwell.app.data.repositories.*
import com.example.mindwell.app.domain.repositories.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bind_auth_repository(
        impl: AuthRepositoryImpl
    ): AuthRepository
    
    @Binds
    @Singleton
    abstract fun bind_form_repository(
        impl: FormRepositoryImpl
    ): FormRepository
    
    @Binds
    @Singleton
    abstract fun bind_checkin_repository(
        checkin_repository: CheckinRepositoryImpl
    ): CheckinRepository
    
    @Binds
    @Singleton
    abstract fun bind_summary_repository(
        impl: SummaryRepositoryImpl
    ): SummaryRepository
    
    @Binds
    @Singleton
    abstract fun bind_user_preference_repository(
        impl: UserPreferenceRepositoryImpl
    ): UserPreferenceRepository
    
    @Binds
    @Singleton
    abstract fun bind_preference_repository(
        impl: PreferenceRepositoryImpl
    ): PreferenceRepository
    
    @Binds
    @Singleton
    abstract fun bind_onboarding_repository(
        impl: OnboardingRepositoryImpl
    ): OnboardingRepository
    
    @Binds
    @Singleton
    abstract fun bind_reminder_repository(
        impl: ReminderRepositoryImpl
    ): ReminderRepository
    
    @Binds
    @Singleton
    abstract fun bind_report_repository(
        impl: ReportRepositoryImpl
    ): ReportRepository
} 