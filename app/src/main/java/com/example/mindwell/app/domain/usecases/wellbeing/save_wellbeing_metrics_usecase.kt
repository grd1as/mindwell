package com.example.mindwell.app.domain.usecases.wellbeing

import com.example.mindwell.app.domain.entities.WellbeingMetrics
import com.example.mindwell.app.domain.repositories.WellbeingMetricsRepository
import javax.inject.Inject

/**
 * Caso de uso para salvar métricas de bem-estar.
 */
class SaveWellbeingMetricsUseCase @Inject constructor(
    private val wellbeingMetricsRepository: WellbeingMetricsRepository
) {
    /**
     * Salva métricas de bem-estar.
     * @param metrics Métricas a serem salvas
     * @return true se as métricas foram salvas com sucesso
     */
    suspend operator fun invoke(metrics: WellbeingMetrics): Result<Boolean> = runCatching {
        wellbeingMetricsRepository.saveWellbeingMetrics(metrics)
    }
} 