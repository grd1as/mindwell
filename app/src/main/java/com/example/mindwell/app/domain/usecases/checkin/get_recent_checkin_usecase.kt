package com.example.mindwell.app.domain.usecases.checkin

import com.example.mindwell.app.domain.entities.CheckIn
import com.example.mindwell.app.domain.repositories.CheckInRepository
import kotlinx.coroutines.flow.Flow

/**
 * Caso de uso para obter check-ins recentes.
 */
class GetRecentCheckInsUseCase(private val repository: CheckInRepository) {
    /**
     * Obtém os check-ins mais recentes, limitado ao número especificado.
     * 
     * @param limit Número máximo de check-ins a retornar
     * @return Flow de lista de check-ins ordenada por timestamp decrescente
     */
    operator fun invoke(limit: Int = 7): Flow<List<CheckIn>> {
        return repository.getRecentCheckIns(limit)
    }
}