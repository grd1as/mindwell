package com.example.mindwell.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * DTO para uma opção com contagem (emoji ou sentimento).
 */
data class OptionCountDTO(
    @SerializedName("optionId")
    val optionId: Int,
    @SerializedName("label")
    val label: String,
    @SerializedName("count")
    val count: Int
)

/**
 * DTO para informações de carga de trabalho.
 */
data class WorkloadInfoDTO(
    @SerializedName("currentAvg")
    val currentAvg: Double,
    @SerializedName("previousAvg")
    val previousAvg: Double,
    @SerializedName("percentChange")
    val percentChange: Double
)

/**
 * DTO para o resumo mensal dos check-ins.
 */
data class MonthlySummaryDTO(
    @SerializedName("period")
    val period: String,
    @SerializedName("totalCheckins")
    val totalCheckins: Int,
    @SerializedName("predominantEmoji")
    val predominantEmoji: List<OptionCountDTO>,
    @SerializedName("predominantSentiment")
    val predominantSentiment: List<OptionCountDTO>,
    @SerializedName("trend")
    val trend: String,
    @SerializedName("workload")
    val workload: WorkloadInfoDTO
) 