package com.example.mindwell.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * DTO para denúncias/reports.
 */
data class ReportDTO(
    @SerializedName("category")
    val category: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("tags")
    val tags: List<String>
) 