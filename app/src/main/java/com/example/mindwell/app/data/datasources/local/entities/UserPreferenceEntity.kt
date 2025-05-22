package com.example.mindwell.app.data.datasources.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade que representa as preferências do usuário.
 */
@Entity(tableName = "user_preferences")
data class UserPreferenceEntity(
    @PrimaryKey
    val id: String = "user_preferences",
    val name: String,
    val email: String,
    val enableNotifications: Boolean,
    val checkinTime: String,
    val theme: String,
    val language: String
) 