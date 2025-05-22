package com.example.mindwell.app.data.repositories

import com.example.mindwell.app.domain.entities.Preference
import com.example.mindwell.app.domain.repositories.PreferenceRepository
import com.example.mindwell.app.domain.repositories.UserPreferenceRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do repositório de preferências que usa dados mockados.
 */
@Singleton
class UserPreferenceRepositoryImpl @Inject constructor() : UserPreferenceRepository {
    
    // Simulando armazenamento local
    private var mockPreference = Preference(
        name = "Usuário",
        notificationsEnabled = true
    )
    
    override suspend fun getPreferences(): Preference {
        // Simulando uma chamada de rede
        return mockPreference
    }
    
    override suspend fun updatePreferences(preference: Preference) {
        // Simulando uma atualização
        mockPreference = preference
    }
}

/**
 * Implementação do repositório de preferências alternativo.
 * Esta classe implementa a interface PreferenceRepository.
 */
@Singleton
class PreferenceRepositoryImpl @Inject constructor() : PreferenceRepository {
    
    // Reusing the same mock data as UserPreferenceRepositoryImpl
    private var mockPreference = Preference(
        name = "Usuário",
        notificationsEnabled = true
    )
    
    override suspend fun getPreferences(): Preference {
        // Simulando uma chamada de rede
        return mockPreference
    }
    
    override suspend fun updatePreferences(preferences: Preference) {
        // Simulando uma atualização
        mockPreference = preferences
    }
} 