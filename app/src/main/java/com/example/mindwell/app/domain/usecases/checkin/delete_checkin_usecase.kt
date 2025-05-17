package com.example.mindwell.app.domain.usecases.checkin

import com.example.mindwell.app.domain.repositories.CheckInRepository

/**
 * Caso de uso para excluir um check-in.
 */
class DeleteCheckInUseCase(private val repository: CheckInRepository) {
    /**
     * Exclui um check-in específico pelo ID.
     * 
     * @param id ID do check-in a ser excluído
     * @return Result indicando sucesso ou falha na operação
     */
    suspend operator fun invoke(id: Long): Result<Boolean> {
        return try {
            val success = repository.deleteCheckIn(id)
            if (success) {
                Result.success(true)
            } else {
                Result.failure(Exception("Check-in não encontrado ou não pôde ser excluído"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}