package com.example.mindwell.app.domain.repositories

import com.example.mindwell.app.domain.entities.CheckIn
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Interface para acesso a operações de check-in diário.
 */
interface CheckInRepository {
    /**
     * Salva um novo check-in.
     * @return ID do check-in salvo
     */
    suspend fun saveCheckIn(checkIn: CheckIn): Long
    
    /**
     * Obtém um check-in específico pelo ID.
     */
    suspend fun getCheckInById(id: Long): CheckIn?
    
    /**
     * Obtém check-ins para uma data específica.
     */
    fun getCheckInsForDate(date: LocalDate): Flow<List<CheckIn>>
    
    /**
     * Obtém check-ins recentes, limitado pelo número especificado.
     */
    fun getRecentCheckIns(limit: Int): Flow<List<CheckIn>>
    
    /**
     * Exclui um check-in pelo ID.
     * @return true se o check-in foi excluído com sucesso
     */
    suspend fun deleteCheckIn(id: Long): Boolean
    
    /**
     * Obtém check-ins para um período específico.
     * @param startDate Data inicial do período
     * @param endDate Data final do período
     * @return Flow com os check-ins do período
     */
    fun getCheckInsForPeriod(startDate: LocalDate, endDate: LocalDate): Flow<List<CheckIn>>
}