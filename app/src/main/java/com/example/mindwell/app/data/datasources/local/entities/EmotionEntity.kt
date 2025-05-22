package com.example.mindwell.app.data.datasources.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade que representa uma emoção/estado de humor.
 */
@Entity(tableName = "emotions")
data class EmotionEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val emoji: String,
    val value: Int
) 