package com.example.mindwell.app.domain.usecases.onboarding

import com.example.mindwell.app.data.repositories.OnboardingRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Caso de uso para verificar se é a primeira vez que o usuário abre o app.
 */
interface IsFirstTimeUseCase {
    /**
     * Verifica se é a primeira vez que o usuário abre o app.
     * @return Flow com o resultado da verificação
     */
    operator fun invoke(): Flow<Result<Boolean>>
}

/**
 * Implementação do caso de uso para verificar primeira vez.
 */
class IsFirstTimeUseCaseImpl @Inject constructor(
    private val onboarding_repository: OnboardingRepositoryImpl
) : IsFirstTimeUseCase {
    
    override fun invoke(): Flow<Result<Boolean>> = flow {
        try {
            val is_first_time = onboarding_repository.is_first_time()
            emit(Result.success(is_first_time))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 