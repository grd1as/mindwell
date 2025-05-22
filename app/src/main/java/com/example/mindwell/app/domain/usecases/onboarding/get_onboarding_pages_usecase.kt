package com.example.mindwell.app.domain.usecases.onboarding

import com.example.mindwell.app.domain.entities.OnboardingPage
import com.example.mindwell.app.domain.repositories.OnboardingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Caso de uso para obter as páginas do onboarding.
 */
interface GetOnboardingPagesUseCase {
    /**
     * Obtém todas as páginas do onboarding.
     * @return Flow com o resultado da operação contendo a lista de páginas
     */
    operator fun invoke(): Flow<Result<List<OnboardingPage>>>
}

/**
 * Implementação do caso de uso para obter as páginas do onboarding.
 */
class GetOnboardingPagesUseCaseImpl @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) : GetOnboardingPagesUseCase {
    
    override operator fun invoke(): Flow<Result<List<OnboardingPage>>> = flow {
        try {
            val pages = onboardingRepository.getOnboardingPages()
            emit(Result.success(pages))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 