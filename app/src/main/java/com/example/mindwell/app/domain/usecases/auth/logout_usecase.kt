package com.example.mindwell.app.domain.usecases.auth

import com.example.mindwell.app.domain.repositories.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Caso de uso para realizar logout.
 */
interface LogoutUseCase {
    /**
     * Realiza logout do usuário.
     * @return Flow com o resultado da operação
     */
    operator fun invoke(): Flow<Result<Unit>>
}

/**
 * Implementação do caso de uso para logout.
 */
class LogoutUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
) : LogoutUseCase {
    override operator fun invoke(): Flow<Result<Unit>> = flow {
        try {
            authRepository.logout()
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 