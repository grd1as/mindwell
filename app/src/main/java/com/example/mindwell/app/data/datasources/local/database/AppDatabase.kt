package com.example.mindwell.app.data.datasources.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mindwell.app.data.datasources.local.dao.CheckinDao
import com.example.mindwell.app.data.datasources.local.dao.EmotionDao
import com.example.mindwell.app.data.datasources.local.entities.CheckinEntity
import com.example.mindwell.app.data.datasources.local.entities.EmotionEntity
import com.example.mindwell.app.data.datasources.local.utils.DateConverters

/**
 * Banco de dados principal da aplicação.
 */
@Database(
    entities = [
        CheckinEntity::class,
        EmotionEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun checkinDao(): CheckinDao
    abstract fun emotionDao(): EmotionDao
    
    companion object {
        private const val DATABASE_NAME = "mindwell_db"
        
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 