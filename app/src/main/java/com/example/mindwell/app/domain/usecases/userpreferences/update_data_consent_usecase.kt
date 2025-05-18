package com.example.mindwell.app.domain.usecases.userpreferences

import com.example.mindwell.app.domain.repositories.AnonymousUserRepository

/**
 * Caso de uso para atualizar o consentimento para coleta de dados anônimos.
 */
interface UpdateDataConsentUseCase {
    /**
     * Atualiza o consentimento para coleta de dados anônimos.
     * @param hasConsent Indica se o usuário consentiu com a coleta de dados
     */
    suspend operator fun invoke(hasConsent: Boolean)
}

/**
 * Implementação do caso de uso para atualizar o consentimento para coleta de dados anônimos.
 */
class UpdateDataConsentUseCaseImpl(
    private val anonymousUserRepository: AnonymousUserRepository
) : UpdateDataConsentUseCase {
    /**
     * Atualiza o consentimento para coleta de dados anônimos.
     * @param hasConsent Indica se o usuário consentiu com a coleta de dados
     */
    override suspend operator fun invoke(hasConsent: Boolean) {
        anonymousUserRepository.updateDataCollectionConsent(hasConsent)
    }
} 