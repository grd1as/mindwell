package com.example.mindwell.app.domain.usecases.userpreferences

import com.example.mindwell.app.domain.entities.UserPreferences
import com.example.mindwell.app.domain.repositories.UserPreferencesRepository
import javax.inject.Inject

/**
 * Caso de uso para atualizar as preferências do usuário.
 */
class UpdateUserPreferencesUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    /**
     * Atualiza as preferências do usuário.
     * @param preferences Novas preferências do usuário
     * @return true se as preferências foram atualizadas com sucesso
     */
    suspend operator fun invoke(preferences: UserPreferences): Result<Boolean> = runCatching {
        userPreferencesRepository.updateUserPreferences(preferences)
    }
} 