package com.example.mindwell.app.data.datasources.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Entidade que representa um check-in diário.
 */
@Entity(
    tableName = "checkins",
    foreignKeys = [
        ForeignKey(
            entity = EmotionEntity::class,
            parentColumns = ["id"],
            childColumns = ["emotionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("emotionId")]
)
data class CheckinEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val answeredAt: LocalDateTime,
    val emotionId: Long,
    val note: String? = null,
    val streak: Int = 0,
    val syncedWithServer: Boolean = false
) 