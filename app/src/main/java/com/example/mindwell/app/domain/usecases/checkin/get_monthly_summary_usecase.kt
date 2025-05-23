package com.example.mindwell.app.domain.usecases.checkin

import com.example.mindwell.app.domain.entities.MonthlySummary
import com.example.mindwell.app.domain.repositories.CheckinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Caso de uso para obter o resumo mensal dos check-ins.
 */
interface GetMonthlySummaryUseCase {
    /**
     * Obtém o resumo mensal dos check-ins.
     * @param year Ano para filtrar
     * @param month Mês para filtrar (1-12)
     * @return Flow com o resultado contendo o resumo mensal
     */
    operator fun invoke(year: Int, month: Int): Flow<Result<MonthlySummary>>
}

/**
 * Implementação do caso de uso para obter resumo mensal.
 */
class GetMonthlySummaryUseCaseImpl @Inject constructor(
    private val checkin_repository: CheckinRepository
) : GetMonthlySummaryUseCase {
    
    override operator fun invoke(year: Int, month: Int): Flow<Result<MonthlySummary>> = flow {
        try {
            val summary = checkin_repository.get_monthly_summary(year, month)
            emit(Result.success(summary))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 