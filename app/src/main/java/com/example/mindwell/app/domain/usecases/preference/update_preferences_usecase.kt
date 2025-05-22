package com.example.mindwell.app.domain.usecases.preference

import com.example.mindwell.app.domain.entities.Preference
import com.example.mindwell.app.domain.repositories.UserPreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Caso de uso para atualizar as preferências do usuário.
 */
interface UpdatePreferencesUseCase {
    /**
     * Atualiza as preferências do usuário.
     * @param preference Novas preferências a serem salvas
     * @return Flow com o resultado da operação
     */
    operator fun invoke(preference: Preference): Flow<Result<Unit>>
}

/**
 * Implementação do caso de uso para atualizar preferências.
 */
class UpdatePreferencesUseCaseImpl @Inject constructor(
    private val preferenceRepository: UserPreferenceRepository
) : UpdatePreferencesUseCase {
    
    override operator fun invoke(preference: Preference): Flow<Result<Unit>> = flow {
        try {
            preferenceRepository.updatePreferences(preference)
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 