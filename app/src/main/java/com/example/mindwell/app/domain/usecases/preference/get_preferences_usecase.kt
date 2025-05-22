package com.example.mindwell.app.domain.usecases.preference

import com.example.mindwell.app.domain.entities.Preference
import com.example.mindwell.app.domain.repositories.PreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Caso de uso para obter as preferências do usuário.
 */
interface GetPreferencesUseCase {
    /**
     * Obtém as preferências atuais do usuário.
     * @return Flow com o resultado contendo as preferências
     */
    operator fun invoke(): Flow<Result<Preference>>
}

/**
 * Implementação do caso de uso para obter as preferências do usuário.
 */
class GetPreferencesUseCaseImpl @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) : GetPreferencesUseCase {
    override operator fun invoke(): Flow<Result<Preference>> = flow {
        try {
            val preferences = preferenceRepository.getPreferences()
            emit(Result.success(preferences))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 