package com.example.mindwell.app.domain.usecases.summary

import com.example.mindwell.app.domain.entities.Summary
import com.example.mindwell.app.domain.repositories.SummaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.YearMonth
import javax.inject.Inject

/**
 * Caso de uso para obter resumo mensal dos check-ins.
 */
interface GetMonthlySummaryUseCase {
    /**
     * Obtém resumo mensal dos check-ins.
     * @param month Mês para o qual obter o resumo (default: mês atual)
     * @return Flow com o resultado contendo o resumo
     */
    operator fun invoke(month: YearMonth = YearMonth.now()): Flow<Result<Summary>>
}

/**
 * Implementação do caso de uso para obter resumo mensal dos check-ins.
 */
class GetMonthlySummaryUseCaseImpl @Inject constructor(
    private val summary_repository: SummaryRepository
) : GetMonthlySummaryUseCase {
    override operator fun invoke(month: YearMonth): Flow<Result<Summary>> = flow {
        try {
            val summary = summary_repository.get_checkin_month_summary(month)
            emit(Result.success(summary))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 