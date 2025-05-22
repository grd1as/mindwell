package com.example.mindwell.app.data.datasources.remote

import com.example.mindwell.app.data.model.ReportDTO
import com.example.mindwell.app.data.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fonte de dados remota para denúncias/reports.
 */
@Singleton
class ReportRemoteDataSource @Inject constructor(
    private val apiService: ApiService
) {
    /**
     * Envia uma nova denúncia/report.
     * @param report Dados da denúncia
     * @return true se o envio foi bem-sucedido
     */
    suspend fun submitReport(report: ReportDTO): Boolean {
        try {
            apiService.submitReport(report)
            return true
        } catch (e: Exception) {
            return false
        }
    }
} 