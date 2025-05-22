package com.example.mindwell.app.domain.usecases.auth

import com.example.mindwell.app.domain.entities.User
import com.example.mindwell.app.domain.repositories.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Caso de uso para realizar login com token do Google.
 */
interface LoginUseCase {
    /**
     * Realiza login com token do Google.
     * @param idToken Token do Google
     * @return Flow com o resultado do login
     */
    operator fun invoke(idToken: String): Flow<Result<User>>
}

/**
 * Implementação do caso de uso para login.
 */
class LoginUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
) : LoginUseCase {
    override operator fun invoke(idToken: String): Flow<Result<User>> = flow {
        try {
            val user = authRepository.login(idToken)
            if (user != null) {
                emit(Result.success(user))
            } else {
                emit(Result.failure(Exception("Falha na autenticação")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 