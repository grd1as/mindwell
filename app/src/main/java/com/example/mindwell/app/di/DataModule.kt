package com.example.mindwell.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.mindwell.app.data.datasources.local.dao.AssessmentDao
import com.example.mindwell.app.data.datasources.local.dao.CheckInDao
import com.example.mindwell.app.data.datasources.local.dao.ResourceDao
import com.example.mindwell.app.data.datasources.local.dao.WellbeingMetricsDao
import com.example.mindwell.app.data.datasources.local.database.MindWellDatabase
import com.example.mindwell.app.data.repositories.AssessmentRepositoryImpl
import com.example.mindwell.app.data.repositories.CheckInRepositoryImpl
import com.example.mindwell.app.data.repositories.ResourceRepositoryImpl
import com.example.mindwell.app.data.repositories.UserPreferencesRepositoryImpl
import com.example.mindwell.app.data.repositories.WellbeingMetricsRepositoryImpl
import com.example.mindwell.app.data.repositories.dataStore
import com.example.mindwell.app.domain.repositories.AssessmentRepository
import com.example.mindwell.app.domain.repositories.CheckInRepository
import com.example.mindwell.app.domain.repositories.ResourceRepository
import com.example.mindwell.app.domain.repositories.UserPreferencesRepository
import com.example.mindwell.app.domain.repositories.WellbeingMetricsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    
    // Database
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MindWellDatabase {
        return MindWellDatabase.getInstance(context)
    }
    
    // DAOs
    @Provides
    @Singleton
    fun provideCheckInDao(database: MindWellDatabase): CheckInDao {
        return database.checkInDao()
    }
    
    @Provides
    @Singleton
    fun provideAssessmentDao(database: MindWellDatabase): AssessmentDao {
        return database.assessmentDao()
    }
    
    @Provides
    @Singleton
    fun provideResourceDao(database: MindWellDatabase): ResourceDao {
        return database.resourceDao()
    }
    
    @Provides
    @Singleton
    fun provideWellbeingMetricsDao(database: MindWellDatabase): WellbeingMetricsDao {
        return database.wellbeingMetricsDao()
    }
    
    // DataStore
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }
    
    // Repositories
    @Provides
    @Singleton
    fun provideCheckInRepository(checkInDao: CheckInDao): CheckInRepository {
        return CheckInRepositoryImpl(checkInDao)
    }
    
    @Provides
    @Singleton
    fun provideAssessmentRepository(assessmentDao: AssessmentDao): AssessmentRepository {
        return AssessmentRepositoryImpl(assessmentDao)
    }
    
    @Provides
    @Singleton
    fun provideResourceRepository(resourceDao: ResourceDao): ResourceRepository {
        return ResourceRepositoryImpl(resourceDao)
    }
    
    @Provides
    @Singleton
    fun provideUserPreferencesRepository(dataStore: DataStore<Preferences>): UserPreferencesRepository {
        return UserPreferencesRepositoryImpl(dataStore)
    }
    
    @Provides
    @Singleton
    fun provideWellbeingMetricsRepository(
        wellbeingMetricsDao: WellbeingMetricsDao,
        checkInDao: CheckInDao
    ): WellbeingMetricsRepository {
        return WellbeingMetricsRepositoryImpl(wellbeingMetricsDao, checkInDao)
    }
} 