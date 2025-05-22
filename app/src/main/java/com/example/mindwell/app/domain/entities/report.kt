package com.example.mindwell.app.domain.entities

/**
 * Entidade de domínio representando uma denúncia/report.
 */
data class Report(
    val category: String,
    val description: String,
    val tags: List<String>
) 