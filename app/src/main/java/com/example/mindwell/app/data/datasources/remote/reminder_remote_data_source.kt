package com.example.mindwell.app.data.datasources.remote

import com.example.mindwell.app.data.model.ReminderDTO
import com.example.mindwell.app.data.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fonte de dados remota para lembretes.
 */
@Singleton
class ReminderRemoteDataSource @Inject constructor(
    private val api_service: ApiService
) {
    /**
     * Obtém os lembretes ativos.
     * @param due Se true, retorna apenas lembretes pendentes
     * @return Lista de lembretes
     */
    suspend fun get_reminders(due: Boolean? = null): List<ReminderDTO> {
        return api_service.get_reminders(due)
    }
} 