package com.example.mindwell.app.data.repositories

import com.example.mindwell.app.data.datasources.remote.ReminderRemoteDataSource
import com.example.mindwell.app.data.mappers.ReminderMapper
import com.example.mindwell.app.domain.entities.Reminder
import com.example.mindwell.app.domain.repositories.ReminderRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do repositório de lembretes.
 */
@Singleton
class ReminderRepositoryImpl @Inject constructor(
    private val remote_data_source: ReminderRemoteDataSource
) : ReminderRepository {
    /**
     * Obtém os lembretes ativos.
     * @param due Se true, retorna apenas lembretes pendentes
     * @return Lista de lembretes
     */
    override suspend fun get_reminders(due: Boolean): List<Reminder> {
        val reminders_dto = remote_data_source.get_reminders(due)
        return ReminderMapper.mapToDomain(reminders_dto)
    }
} 