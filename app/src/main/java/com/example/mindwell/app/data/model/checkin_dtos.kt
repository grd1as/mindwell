package com.example.mindwell.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * DTO para página de check-ins.
 */
data class CheckinPageDTO(
    @SerializedName("page")
    val page: Int,
    
    @SerializedName("size")
    val size: Int,
    
    @SerializedName("totalPages")
    val totalPages: Int,
    
    @SerializedName("totalItems")
    val totalItems: Int,
    
    @SerializedName("items")
    val items: List<CheckinDTO>
)

/**
 * DTO para check-in.
 */
data class CheckinDTO(
    @SerializedName("checkinId")
    val checkinId: Int,
    
    @SerializedName("timestamp")
    val timestamp: String,
    
    @SerializedName("answers")
    val answers: List<CheckinAnswerDTO>,
    
    @SerializedName("streak")
    val streak: Int? = null
)

/**
 * DTO para resposta de check-in.
 */
data class CheckinAnswerDTO(
    @SerializedName("questionId")
    val questionId: Int,
    
    @SerializedName("optionId")
    val optionId: Int,
    
    @SerializedName("value")
    val value: String
) 