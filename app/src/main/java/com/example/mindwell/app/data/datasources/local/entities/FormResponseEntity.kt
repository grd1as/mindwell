package com.example.mindwell.app.data.datasources.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Entidade que representa uma resposta a um formulário.
 */
@Entity(tableName = "form_responses")
data class FormResponseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val formId: Long,
    val formType: String,
    val formName: String,
    val answeredAt: LocalDateTime,
    val responseData: String, // JSON com todas as respostas
    val synced: Boolean = false // Indica se foi sincronizado com o servidor
) 