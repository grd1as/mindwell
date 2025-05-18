package com.example.mindwell.app.data.datasources.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.mindwell.app.data.datasources.local.database.Converters
import com.example.mindwell.app.domain.entities.ResourceType

/**
 * Entidade Room para recursos educativos.
 */
@Entity(tableName = "resources")
@TypeConverters(Converters::class)
data class ResourceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val type: ResourceType,
    val tagsJson: String, // Tags armazenadas em JSON
    val content: String,
    val isRecommended: Boolean
) 