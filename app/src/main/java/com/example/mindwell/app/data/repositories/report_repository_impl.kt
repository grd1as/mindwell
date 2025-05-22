package com.example.mindwell.app.data.repositories

import com.example.mindwell.app.data.datasources.remote.ReportRemoteDataSource
import com.example.mindwell.app.data.mappers.ReportMapper
import com.example.mindwell.app.domain.entities.Report
import com.example.mindwell.app.domain.repositories.ReportRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do repositório de reports.
 */
@Singleton
class ReportRepositoryImpl @Inject constructor(
    private val remoteDataSource: ReportRemoteDataSource
) : ReportRepository {
    /**
     * Envia um report.
     * @param report Dados do report
     * @return ID do report criado
     */
    override suspend fun submitReport(report: Report): Int {
        val reportDto = ReportMapper.mapToDto(report)
        return remoteDataSource.submitReport(reportDto)
    }
} 