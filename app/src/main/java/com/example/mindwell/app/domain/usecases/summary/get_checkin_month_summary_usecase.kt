package com.example.mindwell.app.domain.usecases.summary

import com.example.mindwell.app.domain.entities.Summary
import com.example.mindwell.app.domain.repositories.SummaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.YearMonth
import javax.inject.Inject

/**
 * Caso de uso para obter resumo mensal de check-ins.
 */
interface GetCheckinMonthSummaryUseCase {
    /**
     * Obtém resumo mensal de check-ins.
     * @param month Mês para o qual obter o resumo
     * @return Flow com o resultado contendo o resumo mensal
     */
    operator fun invoke(month: YearMonth): Flow<Result<Summary>>
}

/**
 * Implementação do caso de uso para obter resumo mensal de check-ins.
 */
class GetCheckinMonthSummaryUseCaseImpl @Inject constructor(
    private val summary_repository: SummaryRepository
) : GetCheckinMonthSummaryUseCase {
    override operator fun invoke(month: YearMonth): Flow<Result<Summary>> = flow {
        try {
            val summary = summary_repository.get_checkin_month_summary(month)
            emit(Result.success(summary))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 