package com.example.mindwell.app.domain.usecases.checkin

import com.example.mindwell.app.domain.entities.CheckIn
import com.example.mindwell.app.domain.repositories.CheckInRepository
import java.time.LocalDateTime

/**
 * Caso de uso para criar um novo check-in.
 */
class CreateCheckInUseCase(private val repository: CheckInRepository) {
    /**
     * Cria um novo check-in com os níveis de humor e estresse especificados.
     * 
     * @param moodLevel Nível de humor (1-5)
     * @param stressLevel Nível de estresse (1-5)
     * @param notes Notas opcionais do usuário
     * @return Result contendo o ID do check-in criado ou erro
     */
    suspend operator fun invoke(moodLevel: Int, stressLevel: Int, notes: String? = null): Result<Long> {
        return try {
            val checkIn = CheckIn(
                timestamp = LocalDateTime.now(),
                moodLevel = moodLevel,
                stressLevel = stressLevel,
                notes = notes
            )
            val id = repository.saveCheckIn(checkIn)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
