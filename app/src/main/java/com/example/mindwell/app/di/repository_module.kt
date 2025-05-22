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
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository
    
    @Binds
    @Singleton
    abstract fun bindFormRepository(
        impl: FormRepositoryImpl
    ): FormRepository
    
    @Binds
    @Singleton
    abstract fun bindCheckinRepository(
        impl: CheckinRepositoryImpl
    ): CheckinRepository
    
    @Binds
    @Singleton
    abstract fun bindSummaryRepository(
        impl: SummaryRepositoryImpl
    ): SummaryRepository
    
    @Binds
    @Singleton
    abstract fun bindUserPreferenceRepository(
        impl: UserPreferenceRepositoryImpl
    ): UserPreferenceRepository
    
    @Binds
    @Singleton
    abstract fun bindPreferenceRepository(
        impl: PreferenceRepositoryImpl
    ): PreferenceRepository
    
    @Binds
    @Singleton
    abstract fun bindReminderRepository(
        impl: ReminderRepositoryImpl
    ): ReminderRepository
    
    @Binds
    @Singleton
    abstract fun bindReportRepository(
        impl: ReportRepositoryImpl
    ): ReportRepository
} 