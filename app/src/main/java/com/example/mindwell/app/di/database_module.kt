package com.example.mindwell.app.di

import android.content.Context
import com.example.mindwell.app.data.datasources.local.dao.CheckinDao
import com.example.mindwell.app.data.datasources.local.dao.EmotionDao
import com.example.mindwell.app.data.datasources.local.database.AppDatabase
import com.example.mindwell.app.data.datasources.local.database.DatabaseInitializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provide_app_database(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }
    
    @Provides
    @Singleton
    fun provide_checkin_dao(database: AppDatabase): CheckinDao {
        return database.checkinDao()
    }
    
    @Provides
    @Singleton
    fun provide_emotion_dao(database: AppDatabase): EmotionDao {
        return database.emotionDao()
    }
    
    @Provides
    @Singleton
    fun provide_database_initializer(emotion_dao: EmotionDao): DatabaseInitializer {
        return DatabaseInitializer(emotion_dao)
    }
} 