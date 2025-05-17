package com.example.mindwell.app.domain.usecases.checkin

import com.example.mindwell.app.domain.entities.CheckIn
import com.example.mindwell.app.domain.repositories.CheckInRepository

/**
 * Caso de uso para obter um check-in específico pelo ID.
 */
class GetCheckInByIdUseCase(private val repository: CheckInRepository) {
    /**
     * Obtém um check-in específico pelo ID.
     * 
     * @param id ID do check-in a ser obtido
     * @return Result contendo o check-in ou erro se não encontrado
     */
    suspend operator fun invoke(id: Long): Result<CheckIn> {
        return try {
            val checkIn = repository.getCheckInById(id)
            if (checkIn != null) {
                Result.success(checkIn)
            } else {
                Result.failure(Exception("Check-in não encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}