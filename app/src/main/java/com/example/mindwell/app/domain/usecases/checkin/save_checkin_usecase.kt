package com.example.mindwell.app.domain.usecases.checkin

import com.example.mindwell.app.domain.entities.CheckIn
import com.example.mindwell.app.domain.repositories.CheckInRepository
import javax.inject.Inject

/**
 * Caso de uso para salvar um check-in.
 */
class SaveCheckInUseCase @Inject constructor(
    private val repository: CheckInRepository
) {
    /**
     * Salva um check-in no repositório.
     * 
     * @param checkIn O check-in a ser salvo
     * @return Result contendo o ID do check-in salvo em caso de sucesso, ou uma exceção em caso de falha
     */
    suspend operator fun invoke(checkIn: CheckIn): Result<Long> = runCatching {
        repository.saveCheckIn(checkIn)
    }
} 