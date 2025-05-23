package com.example.mindwell.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * DTO para lista de recursos.
 */
data class ResourceDTO(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("categoryId")
    val categoryId: String,
    
    @SerializedName("durationMinutes")
    val durationMinutes: Int
)

/**
 * DTO para detalhes de recurso.
 */
data class ResourceDetailDTO(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("categoryId")
    val categoryId: String,
    
    @SerializedName("durationMinutes")
    val durationMinutes: Int,
    
    @SerializedName("steps")
    val steps: List<String> = emptyList(),
    
    @SerializedName("completed")
    val completed: Boolean = false
)

/**
 * DTO para categoria de recurso.
 */
data class ResourceCategoryDTO(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("description")
    val description: String
) 