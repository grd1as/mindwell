package com.example.mindwell.app.domain.usecases.summary

import com.example.mindwell.app.domain.entities.Summary
import com.example.mindwell.app.domain.repositories.SummaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Caso de uso para obter resumo semanal de check-ins.
 */
interface GetCheckinWeekSummaryUseCase {
    /**
     * Obtém resumo semanal de check-ins.
     * @param year Ano da semana
     * @param weekNumber Número da semana no ano
     * @return Flow com o resultado contendo o resumo semanal
     */
    operator fun invoke(year: Int, weekNumber: Int): Flow<Result<Summary>>
}

/**
 * Implementação do caso de uso para obter resumo semanal de check-ins.
 */
class GetCheckinWeekSummaryUseCaseImpl @Inject constructor(
    private val summaryRepository: SummaryRepository
) : GetCheckinWeekSummaryUseCase {
    override operator fun invoke(year: Int, weekNumber: Int): Flow<Result<Summary>> = flow {
        try {
            val summary = summaryRepository.getCheckinWeekSummary(year, weekNumber)
            emit(Result.success(summary))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 