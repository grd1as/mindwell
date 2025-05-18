package com.example.mindwell.app.domain.usecases.reminder

import com.example.mindwell.app.domain.entities.Reminder
import com.example.mindwell.app.domain.entities.ReminderPriority
import com.example.mindwell.app.domain.entities.ReminderType
import com.example.mindwell.app.domain.repositories.ReminderRepository
import com.example.mindwell.app.domain.repositories.WellbeingMetricsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime

/**
 * Caso de uso para gerar dicas personalizadas com base no histórico do usuário.
 */
interface GeneratePersonalizedTipsUseCase {
    /**
     * Gera dicas personalizadas com base no histórico do usuário.
     * 
     * @param count Número máximo de dicas a serem geradas
     * @return Flow com o resultado da operação
     */
    operator fun invoke(count: Int = 3): Flow<Result<List<Reminder>>>
}

/**
 * Implementação do caso de uso para gerar dicas personalizadas com base no histórico do usuário.
 */
class GeneratePersonalizedTipsUseCaseImpl(
    private val reminderRepository: ReminderRepository,
    private val wellbeingMetricsRepository: WellbeingMetricsRepository
) : GeneratePersonalizedTipsUseCase {
    
    /**
     * Gera dicas personalizadas com base no histórico do usuário.
     * 
     * @param count Número máximo de dicas a serem geradas
     * @return Flow com o resultado da operação
     */
    override operator fun invoke(count: Int): Flow<Result<List<Reminder>>> = flow {
        try {
            // Create default tips
            val defaultTips = listOf(
                Reminder(
                    title = "Dica de Bem-estar",
                    message = "Pratique alguma atividade física leve hoje",
                    type = ReminderType.WELLBEING_TIP,
                    scheduledTime = LocalDateTime.now(),
                    priority = ReminderPriority.MEDIUM,
                    tags = listOf("bem-estar", "saúde", "autocuidado")
                )
            )
            
            emit(Result.success(defaultTips.take(count)))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 