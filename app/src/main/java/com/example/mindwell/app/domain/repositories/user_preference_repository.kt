package com.example.mindwell.app.domain.repositories

import com.example.mindwell.app.domain.entities.Preference

/**
 * Repositório para gerenciar as preferências do usuário.
 */
interface UserPreferenceRepository {
    /**
     * Obtém as preferências do usuário atual.
     * @return Preferências do usuário
     */
    suspend fun getPreferences(): Preference
    
    /**
     * Atualiza as preferências do usuário.
     * @param preference Novas preferências
     */
    suspend fun updatePreferences(preference: Preference)
} 