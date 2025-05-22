package com.example.mindwell.app.domain.usecases.summary

import com.example.mindwell.app.domain.entities.Summary
import com.example.mindwell.app.domain.repositories.SummaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject

/**
 * Caso de uso para obter resumo semanal dos check-ins.
 */
interface GetWeeklySummaryUseCase {
    /**
     * Obtém resumo semanal dos check-ins.
     * @param date Data para a qual obter o resumo (default: hoje)
     * @return Flow com o resultado contendo o resumo
     */
    operator fun invoke(date: LocalDate = LocalDate.now()): Flow<Result<Summary>>
}

/**
 * Implementação do caso de uso para obter resumo semanal dos check-ins.
 */
class GetWeeklySummaryUseCaseImpl @Inject constructor(
    private val summaryRepository: SummaryRepository
) : GetWeeklySummaryUseCase {
    override operator fun invoke(date: LocalDate): Flow<Result<Summary>> = flow {
        try {
            val weekFields = WeekFields.of(Locale.getDefault())
            val year = date.year
            val weekNumber = date.get(weekFields.weekOfWeekBasedYear())
            
            val summary = summaryRepository.getCheckinWeekSummary(year, weekNumber)
            emit(Result.success(summary))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 