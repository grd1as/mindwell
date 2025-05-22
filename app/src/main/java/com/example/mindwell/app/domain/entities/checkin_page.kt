package com.example.mindwell.app.domain.entities

/**
 * Entidade de domínio representando uma página de check-ins.
 */
data class CheckinPage(
    val page: Int,
    val size: Int,
    val totalPages: Int,
    val totalItems: Int,
    val items: List<Checkin>
) 