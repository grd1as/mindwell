package com.example.mindwell.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * DTO para totais por dia da semana
 */
data class WeekdayTotalDTO(
    @SerializedName("weekday")
    val weekday: Int, // 0=domingo, 1=segunda, etc.
    
    @SerializedName("total")
    val total: Int
)

/**
 * DTO para resumo diário
 */
data class DailySummaryDTO(
    @SerializedName("weekdayTotals")
    val weekdayTotals: List<WeekdayTotalDTO>,
    
    @SerializedName("peakWeekdays")
    val peakWeekdays: List<Int>, // Dias com mais check-ins
    
    @SerializedName("lowWeekdays")
    val lowWeekdays: List<Int> // Dias com menos check-ins
)

/**
 * DTO para opção de emoji/sentimento
 */
data class MoodOptionDTO(
    @SerializedName("optionId")
    val optionId: Int,
    
    @SerializedName("label")
    val label: String
)

/**
 * DTO para humor semanal
 */
data class WeeklyMoodDTO(
    @SerializedName("weekStart")
    val weekStart: String, // formato YYYY-MM-DD
    
    @SerializedName("predominantEmoji")
    val predominantEmoji: MoodOptionDTO,
    
    @SerializedName("predominantSentiment")
    val predominantSentiment: MoodOptionDTO
)

/**
 * DTO para análise de tendência mensal
 */
data class MonthlyTrendDTO(
    @SerializedName("period")
    val period: String, // formato YYYY-MM
    
    @SerializedName("dailySummary")
    val dailySummary: DailySummaryDTO,
    
    @SerializedName("weeklyMood")
    val weeklyMood: List<WeeklyMoodDTO>,
    
    @SerializedName("overallTrend")
    val overallTrend: String
) 