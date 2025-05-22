package com.example.mindwell.app.domain.usecases.checkin

import com.example.mindwell.app.domain.entities.Checkin
import com.example.mindwell.app.domain.repositories.CheckinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Caso de uso para obter o último check-in do usuário.
 */
interface GetLastCheckinUseCase {
    /**
     * Obtém o último check-in do usuário com informações de streak.
     * @return Flow com o resultado contendo o último check-in
     */
    operator fun invoke(): Flow<Result<Checkin>>
}

/**
 * Implementação do caso de uso para obter o último check-in do usuário.
 */
class GetLastCheckinUseCaseImpl @Inject constructor(
    private val checkin_repository: CheckinRepository
) : GetLastCheckinUseCase {
    override operator fun invoke(): Flow<Result<Checkin>> = flow {
        try {
            // Obtém a primeira página com apenas 1 check-in
            val checkin_page = checkin_repository.get_checkins(page = 0, size = 1)
            val last_checkin = checkin_page.items.firstOrNull() ?: throw NoSuchElementException("Nenhum check-in encontrado")
            emit(Result.success(last_checkin))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 