package com.example.mindwell.app.domain.usecases.userpreferences

import com.example.mindwell.app.domain.repositories.AnonymousUserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Caso de uso para verificar se o usuário já completou o onboarding.
 */
interface HasCompletedOnboardingUseCase {
    /**
     * Verifica se o usuário já completou o onboarding.
     * @return Flow com valor booleano indicando se o onboarding foi concluído
     */
    operator fun invoke(): Flow<Boolean>
}

/**
 * Caso de uso para marcar o onboarding como concluído.
 */
interface CompleteOnboardingUseCase {
    /**
     * Marca o onboarding como concluído.
     */
    suspend operator fun invoke()
}

/**
 * Implementação do caso de uso para verificar se o usuário já completou o onboarding.
 */
class HasCompletedOnboardingUseCaseImpl(
    private val anonymousUserRepository: AnonymousUserRepository
) : HasCompletedOnboardingUseCase {
    /**
     * Verifica se o usuário já completou o onboarding.
     * @return Flow com valor booleano indicando se o onboarding foi concluído
     */
    override operator fun invoke(): Flow<Boolean> {
        return anonymousUserRepository.hasCompletedOnboarding()
    }
}

/**
 * Implementação do caso de uso para marcar o onboarding como concluído.
 */
class CompleteOnboardingUseCaseImpl(
    private val anonymousUserRepository: AnonymousUserRepository
) : CompleteOnboardingUseCase {
    /**
     * Marca o onboarding como concluído.
     */
    override suspend operator fun invoke() {
        anonymousUserRepository.markOnboardingCompleted()
    }
}

/**
 * Mock implementação do caso de uso para verificar se o usuário já completou o onboarding.
 */
class MockHasCompletedOnboardingUseCase : HasCompletedOnboardingUseCase {
    /**
     * Simula a verificação se o usuário já completou o onboarding.
     * @return Flow com valor false para simular que o onboarding não foi concluído
     */
    override operator fun invoke(): Flow<Boolean> = flow {
        // Simular atraso
        kotlinx.coroutines.delay(500)
        emit(false) // Por padrão, indicar que o onboarding não foi concluído
    }
}

/**
 * Mock implementação do caso de uso para marcar o onboarding como concluído.
 */
class MockCompleteOnboardingUseCase : CompleteOnboardingUseCase {
    /**
     * Simula a marcação do onboarding como concluído.
     */
    override suspend operator fun invoke() {
        // Simular atraso
        kotlinx.coroutines.delay(300)
        // Não faz nada além disso em uma implementação mock
    }
} 