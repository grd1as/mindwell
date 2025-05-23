package com.example.mindwell.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * DTO para lista de formulários.
 */
data class FormDTO(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("code")
    val code: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("type")
    val type: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("nextAllowed")
    val nextAllowed: String?,
    
    @SerializedName("lastAnsweredAt")
    val lastAnsweredAt: String?
)

/**
 * DTO para detalhes de formulário.
 */
data class FormDetailDTO(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("code")
    val code: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("questions")
    val questions: List<QuestionDTO>
)

/**
 * DTO para pergunta de formulário.
 */
data class QuestionDTO(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("ordinal")
    val ordinal: Int,
    
    @SerializedName("type")
    val type: String,
    
    @SerializedName("text")
    val text: String,
    
    @SerializedName("options")
    val options: List<OptionDTO>
)

/**
 * DTO para opção de resposta.
 */
data class OptionDTO(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("value")
    val value: String,
    
    @SerializedName("label")
    val label: String
)

/**
 * DTO para requisição de envio de respostas.
 */
data class FormResponseRequest(
    @SerializedName("answers")
    val answers: List<AnswerDTO>
)

/**
 * DTO para resposta de pergunta.
 */
data class AnswerDTO(
    @SerializedName("questionId")
    val question_id: Long,
    
    @SerializedName("optionId")
    val option_id: Long? = null,
    
    @SerializedName("value")
    val value: Double? = null,
    
    @SerializedName("text")
    val text: String? = null
) 