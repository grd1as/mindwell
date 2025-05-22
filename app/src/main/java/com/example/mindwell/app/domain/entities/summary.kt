package com.example.mindwell.app.domain.entities

/**
 * Entidade de domínio representando um resumo de check-ins por período.
 */
data class Summary(
    val total: Int,
    val breakdown: List<SummaryItem>,
    val overallLevel: String
)

/**
 * Entidade de domínio representando um item do resumo.
 */
data class SummaryItem(
    val value: String,
    val count: Int,
    val percent: Int,
    val level: String
) 