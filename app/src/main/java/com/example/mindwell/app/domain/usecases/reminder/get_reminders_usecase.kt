package com.example.mindwell.app.domain.usecases.reminder

import com.example.mindwell.app.domain.entities.Reminder
import com.example.mindwell.app.domain.repositories.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Caso de uso para obter lembretes ativos.
 */
interface GetRemindersUseCase {
    /**
     * Obtém lembretes ativos.
     * @param due Se true, retorna apenas lembretes pendentes
     * @return Flow com o resultado contendo a lista de lembretes
     */
    operator fun invoke(due: Boolean = false): Flow<Result<List<Reminder>>>
}

/**
 * Implementação do caso de uso para obter lembretes ativos.
 */
class GetRemindersUseCaseImpl @Inject constructor(
    private val reminder_repository: ReminderRepository
) : GetRemindersUseCase {
    override operator fun invoke(due: Boolean): Flow<Result<List<Reminder>>> = flow {
        try {
            val reminders = reminder_repository.get_reminders(due)
            emit(Result.success(reminders))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 