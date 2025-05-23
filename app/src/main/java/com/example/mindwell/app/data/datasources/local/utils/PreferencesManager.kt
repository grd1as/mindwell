package com.example.mindwell.app.data.datasources.local.utils

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gerenciador de preferências do aplicativo usando SharedPreferences.
 */
@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val preferences: SharedPreferences = context.getSharedPreferences(
        PREFERENCES_NAME, 
        Context.MODE_PRIVATE
    )
    
    companion object {
        private const val PREFERENCES_NAME = "mindwell_preferences"
        private const val KEY_IS_FIRST_TIME = "is_first_time"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    }
    
    /**
     * Verifica se é a primeira vez que o usuário abre o app.
     * @return true se for a primeira vez, false caso contrário
     */
    fun isFirstTime(): Boolean {
        return preferences.getBoolean(KEY_IS_FIRST_TIME, true)
    }
    
    /**
     * Marca que o usuário já abriu o app pela primeira vez.
     */
    fun setFirstTimeComplete() {
        preferences.edit()
            .putBoolean(KEY_IS_FIRST_TIME, false)
            .apply()
    }
    
    /**
     * Verifica se o usuário completou o onboarding.
     * @return true se completou, false caso contrário
     */
    fun isOnboardingCompleted(): Boolean {
        return preferences.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }
    
    /**
     * Marca que o usuário completou o onboarding.
     */
    fun setOnboardingCompleted() {
        preferences.edit()
            .putBoolean(KEY_ONBOARDING_COMPLETED, true)
            .apply()
    }
    
    /**
     * Reseta todas as preferências (útil para desenvolvimento/teste).
     */
    fun clearAll() {
        preferences.edit().clear().apply()
    }
} 