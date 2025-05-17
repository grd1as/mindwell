package com.example.mindwell.app.domain.usecases.checkin

import com.example.mindwell.app.domain.entities.CheckIn
import com.example.mindwell.app.domain.repositories.CheckInRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Caso de uso para obter check-ins de uma data específica.
 */
class GetCheckInsForDateUseCase(private val repository: CheckInRepository) {
    /**
     * Obtém todos os check-ins para uma data específica.
     * 
     * @param date Data para a qual buscar check-ins (padrão: hoje)
     * @return Flow de lista de check-ins para a data especificada
     */
    operator fun invoke(date: LocalDate = LocalDate.now()): Flow<List<CheckIn>> {
        return repository.getCheckInsForDate(date)
    }
}