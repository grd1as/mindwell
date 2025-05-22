package com.example.mindwell.app.domain.entities

/**
 * Entidade de domínio representando uma página de check-ins.
 */
data class CheckinPage(
    val page: Int = 0,
    val size: Int = 20,
    val total_pages: Int,
    val total_items: Int,
    val items: List<Checkin>,
    val current_page: Int = 0,
    val total_elements: Int = 0
) 