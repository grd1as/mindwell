package com.example.mindwell.app.data.services

import com.google.gson.annotations.SerializedName

/**
 * Modelos para parsing das respostas JSON do Gemini
 */

data class GeminiResourcesResponse(
    @SerializedName("resources")
    val resources: List<GeminiResourceItem>,
    @SerializedName("personalized_message")
    val personalized_message: String
)

data class GeminiResourceItem(
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("duration_minutes")
    val duration_minutes: Int,
    @SerializedName("difficulty")
    val difficulty: String,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("action_text")
    val action_text: String
)

data class GeminiTipsResponse(
    @SerializedName("tips")
    val tips: List<GeminiTipItem>
)

data class GeminiTipItem(
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("estimated_time")
    val estimated_time: String,
    @SerializedName("priority")
    val priority: String
) 