package com.example.mindwell.app.domain.usecases.wellbeing

import com.example.mindwell.app.domain.entities.WellbeingMetrics
import com.example.mindwell.app.domain.repositories.WellbeingMetricsRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * Caso de uso para gerar métricas de bem-estar para uma data específica.
 */
class GenerateWellbeingMetricsUseCase @Inject constructor(
    private val wellbeingMetricsRepository: WellbeingMetricsRepository
) {
    /**
     * Gera métricas de bem-estar para uma data específica, baseadas em check-ins e avaliações.
     * @param date Data para a qual gerar as métricas
     * @return As métricas geradas ou null se não for possível
     */
    suspend operator fun invoke(date: LocalDate): Result<WellbeingMetrics?> = runCatching {
        wellbeingMetricsRepository.generateWellbeingMetricsForDate(date)
    }
} 