package com.example.mindwell.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * DTO para lembretes.
 */
data class ReminderDTO(
    @SerializedName("formId")
    val formId: Int,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("scheduled")
    val scheduled: String
) 