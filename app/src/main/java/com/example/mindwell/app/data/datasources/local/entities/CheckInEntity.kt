package com.example.mindwell.app.data.datasources.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Entidade Room para check-ins diários.
 */
@Entity(tableName = "checkins")
data class CheckInEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: LocalDateTime,
    val moodLevel: Int,
    val stressLevel: Int,
    val notes: String?
) 