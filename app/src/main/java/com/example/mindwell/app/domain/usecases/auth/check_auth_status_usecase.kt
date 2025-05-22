package com.example.mindwell.app.domain.usecases.auth

import com.example.mindwell.app.domain.repositories.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Caso de uso para verificar se o usuário está autenticado.
 */
interface CheckAuthStatusUseCase {
    /**
     * Verifica se o usuário está autenticado.
     * @return Flow com o resultado da verificação
     */
    operator fun invoke(): Flow<Result<Boolean>>
}

/**
 * Implementação do caso de uso para verificar autenticação.
 */
class CheckAuthStatusUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
) : CheckAuthStatusUseCase {
    override operator fun invoke(): Flow<Result<Boolean>> = flow {
        try {
            val isAuthenticated = authRepository.isAuthenticated()
            emit(Result.success(isAuthenticated))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 