package com.example.mindwell.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * DTO para sentimento disponível no check-in.
 */
data class FeelingDTO(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("label")
    val label: String,
    
    @SerializedName("emoji")
    val emoji: String? = null,
    
    @SerializedName("value")
    val value: Int? = null
) 