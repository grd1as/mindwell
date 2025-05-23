package com.example.mindwell.app.domain.entities

/**
 * Entidade representando uma opção de emoji ou sentimento com contagem.
 */
data class OptionCount(
    val option_id: Int,
    val label: String,
    val count: Int
)

/**
 * Entidade representando informações sobre carga de trabalho.
 */
data class WorkloadInfo(
    val current_avg: Double,
    val previous_avg: Double,
    val percent_change: Double
)

/**
 * Entidade representando o resumo mensal dos check-ins.
 */
data class MonthlySummary(
    val period: String, // formato: "2025-05"
    val total_checkins: Int,
    val predominant_emoji: List<OptionCount>,
    val predominant_sentiment: List<OptionCount>, 
    val trend: String, // "up", "down", "stable"
    val workload: WorkloadInfo
) 