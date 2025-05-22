package com.example.mindwell.app.data.repositories

import com.example.mindwell.app.data.datasources.remote.ReportRemoteDataSource
import com.example.mindwell.app.data.mappers.ReportMapper
import com.example.mindwell.app.domain.entities.Report
import com.example.mindwell.app.domain.repositories.ReportRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do repositório de denúncias/reports.
 */
@Singleton
class ReportRepositoryImpl @Inject constructor(
    private val remoteDataSource: ReportRemoteDataSource
) : ReportRepository {
    /**
     * Envia uma nova denúncia/report.
     * @param report Dados da denúncia
     * @return true se o envio foi bem-sucedido
     */
    override suspend fun submitReport(report: Report): Boolean {
        val reportDto = ReportMapper.mapToDto(report)
        return remoteDataSource.submitReport(reportDto)
    }
} 