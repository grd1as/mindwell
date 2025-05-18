package com.example.mindwell.app.data.datasources.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.mindwell.app.data.datasources.local.database.Converters
import java.time.LocalDate

/**
 * Entidade Room para métricas de bem-estar.
 */
@Entity(tableName = "wellbeing_metrics")
@TypeConverters(Converters::class)
data class WellbeingMetricsEntity(
    @PrimaryKey
    val date: LocalDate,
    val averageMood: Float,
    val averageStress: Float,
    val workloadScore: Int?,
    val environmentScore: Int?,
    val wellbeingScore: Float
) 