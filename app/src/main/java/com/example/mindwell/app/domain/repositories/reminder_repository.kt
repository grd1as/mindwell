package com.example.mindwell.app.domain.repositories

import com.example.mindwell.app.domain.entities.Reminder
import com.example.mindwell.app.domain.entities.ReminderType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Interface para gerenciamento de lembretes e dicas.
 */
interface ReminderRepository {
    /**
     * Obtém todos os lembretes pendentes.
     */
    fun getPendingReminders(): Flow<List<Reminder>>
    
    /**
     * Obtém os lembretes pendentes para o dia atual.
     */
    fun getTodayReminders(): Flow<List<Reminder>>
    
    /**
     * Obtém os lembretes por tipo.
     */
    fun getRemindersByType(type: ReminderType): Flow<List<Reminder>>
    
    /**
     * Obtém lembretes baseados no histórico e perfil do usuário.
     */
    fun getPersonalizedReminders(): Flow<List<Reminder>>
    
    /**
     * Cria um novo lembrete.
     * @return ID do lembrete criado
     */
    suspend fun createReminder(reminder: Reminder): Long
    
    /**
     * Atualiza o status de entrega de um lembrete.
     * @return true se o lembrete foi atualizado com sucesso
     */
    suspend fun markReminderAsDelivered(id: Long): Boolean
    
    /**
     * Cancela um lembrete.
     * @return true se o lembrete foi cancelado com sucesso
     */
    suspend fun cancelReminder(id: Long): Boolean
    
    /**
     * Gera lembretes personalizados com base nos dados do usuário.
     * @param limit Número máximo de lembretes a serem gerados
     * @return Lista de lembretes gerados
     */
    suspend fun generatePersonalizedReminders(limit: Int = 5): List<Reminder>
} 