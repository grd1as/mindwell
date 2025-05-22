package com.example.mindwell.app.domain.usecases.report

import com.example.mindwell.app.domain.entities.Report
import com.example.mindwell.app.domain.repositories.ReportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Caso de uso para enviar uma denúncia/report.
 */
interface SubmitReportUseCase {
    /**
     * Envia uma denúncia/report.
     * @param category Categoria da denúncia
     * @param description Descrição da denúncia
     * @param tags Tags associadas à denúncia
     * @return Flow com o resultado da operação, contendo o ID do report criado ou -1 em caso de falha
     */
    operator fun invoke(
        category: String,
        description: String,
        tags: List<String>
    ): Flow<Result<Int>>
}

/**
 * Implementação do caso de uso para enviar uma denúncia/report.
 */
class SubmitReportUseCaseImpl @Inject constructor(
    private val report_repository: ReportRepository
) : SubmitReportUseCase {
    override operator fun invoke(
        category: String,
        description: String,
        tags: List<String>
    ): Flow<Result<Int>> = flow {
        try {
            val report = Report(category, description, tags)
            val report_id = report_repository.submit_report(report)
            emit(Result.success(report_id))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 