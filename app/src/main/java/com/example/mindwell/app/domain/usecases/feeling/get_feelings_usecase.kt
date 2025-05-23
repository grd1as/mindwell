package com.example.mindwell.app.domain.usecases.feeling

import com.example.mindwell.app.domain.entities.Feeling
import com.example.mindwell.app.domain.repositories.FeelingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Caso de uso para obter lista de sentimentos disponíveis.
 */
interface GetFeelingsUseCase {
    /**
     * Obtém lista de sentimentos disponíveis para check-in.
     * @return Flow com o resultado contendo a lista de sentimentos
     */
    operator fun invoke(): Flow<Result<List<Feeling>>>
}

/**
 * Implementação do caso de uso para obter lista de sentimentos.
 */
class GetFeelingsUseCaseImpl @Inject constructor(
    private val feeling_repository: FeelingRepository
) : GetFeelingsUseCase {
    override operator fun invoke(): Flow<Result<List<Feeling>>> = flow {
        try {
            val feelings = feeling_repository.get_feelings()
            emit(Result.success(feelings))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 