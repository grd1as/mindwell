package com.example.mindwell.app.domain.repositories

import com.example.mindwell.app.domain.entities.Reminder

/**
 * Interface para o repositório de lembretes.
 */
interface ReminderRepository {
    /**
     * Obtém os lembretes ativos.
     * @param due Se true, retorna apenas lembretes pendentes
     * @return Lista de lembretes
     */
    suspend fun getReminders(due: Boolean = false): List<Reminder>
} 