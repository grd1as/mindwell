package com.example.mindwell.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * DTO para página de check-ins.
 */
data class CheckinPageDTO(
    @SerializedName("number")
    val page: Int,
    
    @SerializedName("size")
    val size: Int,
    
    @SerializedName("totalPages")
    val total_pages: Int,
    
    @SerializedName("totalElements")
    val total_items: Int,
    
    @SerializedName("content")
    val items: List<CheckinDTO>
)

/**
 * DTO para check-in.
 */
data class CheckinDTO(
    @SerializedName("checkinId")
    val checkin_id: Int,
    
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
    val question_id: Int,
    
    @SerializedName("optionId")
    val option_id: Int,
    
    @SerializedName("value")
    val value: String
) 