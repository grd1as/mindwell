package com.example.mindwell.app.domain.repositories

import com.example.mindwell.app.domain.entities.Report

/**
 * Interface para o repositório de reports.
 */
interface ReportRepository {
    /**
     * Envia um report.
     * @param report Dados do report
     * @return ID do report criado
     */
    suspend fun submit_report(report: Report): Int
} 