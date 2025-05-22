package com.example.mindwell.app.domain.usecases.onboarding

import com.example.mindwell.app.domain.entities.OnboardingState
import com.example.mindwell.app.domain.repositories.OnboardingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Caso de uso para obter o estado atual do onboarding.
 */
interface GetOnboardingStateUseCase {
    /**
     * Obtém o estado atual do onboarding.
     * @return Flow com o resultado da operação contendo o estado
     */
    operator fun invoke(): Flow<Result<OnboardingState>>
}

/**
 * Implementação do caso de uso para obter o estado do onboarding.
 */
class GetOnboardingStateUseCaseImpl @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) : GetOnboardingStateUseCase {
    
    override fun invoke(): Flow<Result<OnboardingState>> {
        return onboardingRepository.getOnboardingState()
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
    }
} 