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
    val label: String,
    
    @SerializedName("count")
    val count: Int = 0,
    
    @SerializedName("percent")
    val percent: Double = 0.0,
    
    @SerializedName("level")
    val level: String = ""
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

/**
 * DTO para questão de humor
 */
data class MoodQuestionDTO(
    @SerializedName("ordinal")
    val ordinal: Int,
    
    @SerializedName("text")
    val text: String,
    
    @SerializedName("totalResponses")
    val totalResponses: Int,
    
    @SerializedName("options")
    val options: List<MoodOptionDTO>
)

/**
 * DTO para distribuição de humor por mês
 */
data class MoodDistributionDTO(
    @SerializedName("period")
    val period: String,
    
    @SerializedName("questions")
    val questions: List<MoodQuestionDTO>
)

/**
 * DTO para alertas de carga de trabalho
 */
data class WorkloadAlertsDTO(
    @SerializedName("months")
    val months: List<WorkloadMonthDTO>
)

data class WorkloadMonthDTO(
    @SerializedName("period")
    val period: String,
    
    @SerializedName("workloadAvg")
    val workloadAvg: Double,
    
    @SerializedName("alertCount")
    val alertCount: Int
)

/**
 * DTO para diagnóstico de clima organizacional
 */
data class ClimateDiagnosisDTO(
    @SerializedName("period")
    val period: String,
    
    @SerializedName("dimensions")
    val dimensions: List<ClimateDimensionDTO>
)

data class ClimateDimensionDTO(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("score")
    val score: Double,
    
    @SerializedName("status")
    val status: String
) 