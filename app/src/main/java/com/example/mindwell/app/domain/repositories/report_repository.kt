package com.example.mindwell.app.domain.repositories

import com.example.mindwell.app.domain.entities.Report

/**
 * Interface para o repositório de denúncias/reports.
 */
interface ReportRepository {
    /**
     * Envia uma nova denúncia/report.
     * @param report Dados da denúncia
     * @return true se o envio foi bem-sucedido
     */
    suspend fun submitReport(report: Report): Boolean
} 