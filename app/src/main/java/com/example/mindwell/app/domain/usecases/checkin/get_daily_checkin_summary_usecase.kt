package com.example.mindwell.app.domain.usecases.checkin

import com.example.mindwell.app.domain.entities.CheckIn
import com.example.mindwell.app.domain.repositories.CheckInRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * Caso de uso para obter um resumo dos check-ins agrupados por dia.
 * Útil para exibir em gráficos ou históricos.
 */
class GetDailyCheckInSummaryUseCase(private val repository: CheckInRepository) {
    /**
     * Obtém um resumo diário dos check-ins para um período específico.
     * 
     * @param startDate Data inicial do período
     * @param endDate Data final do período (padrão: hoje)
     * @return Flow de mapa com data formatada como chave e estatísticas como valor
     */
    fun invoke(
        startDate: LocalDate, 
        endDate: LocalDate = LocalDate.now()
    ): Flow<Map<String, CheckInDailySummary>> {
        // Obtém check-ins para cada dia no período
        return repository.getCheckInsForPeriod(startDate, endDate).map { checkIns ->
            // Agrupa por data
            checkIns.groupBy { 
                it.timestamp.toLocalDate() 
            }.mapValues { (_, checksForDay) ->
                // Calcula estatísticas para cada dia
                CheckInDailySummary(
                    date = checksForDay.first().timestamp.toLocalDate(),
                    averageMood = checksForDay.map { it.moodLevel }.average().toFloat(),
                    averageStress = checksForDay.map { it.stressLevel }.average().toFloat(),
                    count = checksForDay.size
                )
            }.mapKeys { (date, _) ->
                // Formata a data para exibição
                date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
            }
        }
    }
    
    /**
     * Classe para armazenar o resumo diário de check-ins.
     */
    data class CheckInDailySummary(
        val date: LocalDate,
        val averageMood: Float,
        val averageStress: Float,
        val count: Int
    )
}