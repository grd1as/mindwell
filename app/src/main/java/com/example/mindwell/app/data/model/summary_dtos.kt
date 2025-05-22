package com.example.mindwell.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * DTO para resumo de check-ins.
 */
data class SummaryDTO(
    @SerializedName("total")
    val total: Int,
    
    @SerializedName("breakdown")
    val breakdown: List<SummaryItemDTO>,
    
    @SerializedName("overallLevel")
    val overallLevel: String
)

/**
 * DTO para item de resumo.
 */
data class SummaryItemDTO(
    @SerializedName("value")
    val value: String,
    
    @SerializedName("count")
    val count: Int,
    
    @SerializedName("percent")
    val percent: Int,
    
    @SerializedName("level")
    val level: String
) 