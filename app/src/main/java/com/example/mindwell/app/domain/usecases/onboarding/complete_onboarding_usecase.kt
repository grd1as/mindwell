package com.example.mindwell.app.domain.usecases.onboarding

import com.example.mindwell.app.domain.repositories.OnboardingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Caso de uso para marcar o onboarding como concluído.
 */
interface CompleteOnboardingUseCase {
    /**
     * Marca o onboarding como concluído.
     * @return Flow com o resultado da operação
     */
    operator fun invoke(): Flow<Result<Unit>>
}

/**
 * Implementação do caso de uso para concluir o onboarding.
 */
class CompleteOnboardingUseCaseImpl @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) : CompleteOnboardingUseCase {
    
    override fun invoke(): Flow<Result<Unit>> = flow {
        try {
            onboardingRepository.completeOnboarding()
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 