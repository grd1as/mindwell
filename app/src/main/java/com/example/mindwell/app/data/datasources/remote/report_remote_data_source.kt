package com.example.mindwell.app.data.datasources.remote

import com.example.mindwell.app.data.model.ReportDTO
import com.example.mindwell.app.data.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fonte de dados remota para reports.
 */
@Singleton
class ReportRemoteDataSource @Inject constructor(
    private val api_service: ApiService
) {
    /**
     * Envia um report.
     * @param report Dados do report
     * @return ID do report criado
     */
    suspend fun submit_report(report: ReportDTO): Int {
        val response = api_service.submit_report(report)
        // Extrai o ID do report a partir da URL retornada (exemplo: /reports/42)
        val locationPath = response.location ?: ""
        return locationPath.substringAfterLast("/").toIntOrNull() ?: -1
    }
} 