package com.example.mindwell.app.domain.usecases.userpreferences

import com.example.mindwell.app.domain.entities.AnonymousUser
import com.example.mindwell.app.domain.repositories.AnonymousUserRepository
import kotlinx.coroutines.flow.Flow

/**
 * Caso de uso para obter o usuário anônimo atual.
 */
interface GetAnonymousUserUseCase {
    /**
     * Obtém o usuário anônimo atual ou cria um novo se não existir.
     * @return Flow com o usuário anônimo
     */
    operator fun invoke(): Flow<AnonymousUser>
}

/**
 * Implementação do caso de uso para obter o usuário anônimo.
 */
class GetAnonymousUserUseCaseImpl(
    private val anonymousUserRepository: AnonymousUserRepository
) : GetAnonymousUserUseCase {
    /**
     * Obtém o usuário anônimo atual ou cria um novo se não existir.
     * @return Flow com o usuário anônimo
     */
    override operator fun invoke(): Flow<AnonymousUser> {
        return anonymousUserRepository.getAnonymousUser()
    }
} 