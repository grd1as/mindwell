package com.example.mindwell.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * DTO para preferências do usuário.
 */
data class PreferenceDTO(
    @SerializedName("name")
    val name: String = "",
    
    @SerializedName("notificationsEnabled")
    val notificationsEnabled: Boolean = false
) 