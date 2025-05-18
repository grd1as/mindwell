package com.example.mindwell.app.domain.usecases.checkin

import com.example.mindwell.app.domain.entities.CheckIn
import com.example.mindwell.app.domain.repositories.CheckInRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Caso de uso para obter check-ins recentes.
 */
interface GetRecentCheckInsUseCase {
    /**
     * Obtém os check-ins mais recentes, limitado ao número especificado.
     * 
     * @param limit Número máximo de check-ins a retornar
     * @return Flow com o resultado da operação contendo uma lista de check-ins ordenada por timestamp decrescente
     */
    operator fun invoke(limit: Int = 7): Flow<Result<List<CheckIn>>>
}

/**
 * Implementação do caso de uso para obter check-ins recentes.
 */
class GetRecentCheckInsUseCaseImpl(
    private val repository: CheckInRepository
) : GetRecentCheckInsUseCase {
    /**
     * Obtém os check-ins mais recentes, limitado ao número especificado.
     * 
     * @param limit Número máximo de check-ins a retornar
     * @return Flow com o resultado da operação contendo uma lista de check-ins ordenada por timestamp decrescente
     */
    override operator fun invoke(limit: Int): Flow<Result<List<CheckIn>>> {
        return repository.getRecentCheckIns(limit)
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
    }
}