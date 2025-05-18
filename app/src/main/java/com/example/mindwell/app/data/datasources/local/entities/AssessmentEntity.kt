package com.example.mindwell.app.data.datasources.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.mindwell.app.data.datasources.local.database.Converters
import com.example.mindwell.app.domain.entities.AssessmentType
import java.time.LocalDateTime

/**
 * Entidade Room para avaliações psicossociais.
 */
@Entity(tableName = "assessments")
@TypeConverters(Converters::class)
data class AssessmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: AssessmentType,
    val timestamp: LocalDateTime,
    val responsesJson: String, // Respostas armazenadas em JSON
    val score: Int
) 