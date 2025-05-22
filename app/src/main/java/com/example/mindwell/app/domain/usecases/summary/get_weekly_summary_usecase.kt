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
    private val summary_repository: SummaryRepository
) : GetWeeklySummaryUseCase {
    override operator fun invoke(date: LocalDate): Flow<Result<Summary>> = flow {
        try {
            val week_fields = WeekFields.of(Locale.getDefault())
            val year = date.year
            val week_number = date.get(week_fields.weekOfWeekBasedYear())
            
            val summary = summary_repository.get_checkin_week_summary(year, week_number)
            emit(Result.success(summary))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 