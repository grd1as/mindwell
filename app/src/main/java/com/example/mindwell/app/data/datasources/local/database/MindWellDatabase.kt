package com.example.mindwell.app.data.datasources.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mindwell.app.data.datasources.local.dao.AssessmentDao
import com.example.mindwell.app.data.datasources.local.dao.CheckInDao
import com.example.mindwell.app.data.datasources.local.dao.ResourceDao
import com.example.mindwell.app.data.datasources.local.dao.WellbeingMetricsDao
import com.example.mindwell.app.data.datasources.local.entities.AssessmentEntity
import com.example.mindwell.app.data.datasources.local.entities.CheckInEntity
import com.example.mindwell.app.data.datasources.local.entities.ResourceEntity
import com.example.mindwell.app.data.datasources.local.entities.WellbeingMetricsEntity

/**
 * Banco de dados principal do aplicativo MindWell.
 */
@Database(
    entities = [
        CheckInEntity::class,
        AssessmentEntity::class,
        ResourceEntity::class,
        WellbeingMetricsEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MindWellDatabase : RoomDatabase() {
    
    abstract fun checkInDao(): CheckInDao
    abstract fun assessmentDao(): AssessmentDao
    abstract fun resourceDao(): ResourceDao
    abstract fun wellbeingMetricsDao(): WellbeingMetricsDao
    
    companion object {
        private const val DATABASE_NAME = "mindwell_db"
        
        @Volatile
        private var INSTANCE: MindWellDatabase? = null
        
        fun getInstance(context: Context): MindWellDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MindWellDatabase::class.java,
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