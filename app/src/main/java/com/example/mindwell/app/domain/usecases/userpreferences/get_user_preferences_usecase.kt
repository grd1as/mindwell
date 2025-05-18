package com.example.mindwell.app.domain.usecases.userpreferences

import com.example.mindwell.app.domain.entities.UserPreferences
import com.example.mindwell.app.domain.repositories.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Caso de uso para obter as preferências do usuário.
 */
class GetUserPreferencesUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    /**
     * Obtém as preferências do usuário.
     * @return Flow com as preferências do usuário
     */
    operator fun invoke(): Flow<Result<UserPreferences>> {
        return userPreferencesRepository.getUserPreferences()
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
    }
} 