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
    private val remoteDataSource: ReminderRemoteDataSource
) : ReminderRepository {
    /**
     * Obtém os lembretes ativos.
     * @param due Se true, retorna apenas lembretes pendentes
     * @return Lista de lembretes
     */
    override suspend fun getReminders(due: Boolean): List<Reminder> {
        val remindersDto = remoteDataSource.getReminders(due)
        return ReminderMapper.mapToDomain(remindersDto)
    }
} 