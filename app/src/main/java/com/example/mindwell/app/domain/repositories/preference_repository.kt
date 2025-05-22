package com.example.mindwell.app.domain.repositories

import com.example.mindwell.app.domain.entities.Preference

/**
 * Interface para o repositório de preferências do usuário.
 */
interface PreferenceRepository {
    /**
     * Obtém as preferências do usuário.
     * @return Preferências atuais
     */
    suspend fun getPreferences(): Preference
    
    /**
     * Atualiza as preferências do usuário.
     * @param preferences Novas preferências
     */
    suspend fun updatePreferences(preferences: Preference)
} 