package com.example.mindwell.app.data.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.mindwell.app.domain.entities.AnonymousUser
import com.example.mindwell.app.domain.entities.AppTheme
import com.example.mindwell.app.domain.repositories.AnonymousUserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.util.UUID

// Extensão para facilitar o acesso ao DataStore
private val Context.anonymousUserDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "anonymous_user_prefs"
)

/**
 * Implementação do repositório para gerenciamento do usuário anônimo.
 * Usa DataStore para armazenar os dados de forma segura e persistente.
 */
class AnonymousUserRepositoryImpl(
    private val context: Context
) : AnonymousUserRepository {

    companion object {
        private val DEVICE_ID_KEY = stringPreferencesKey("device_id")
        private val PREFERRED_REMINDER_TIME_KEY = stringPreferencesKey("preferred_reminder_time")
        private val NOTIFICATIONS_ENABLED_KEY = booleanPreferencesKey("notifications_enabled")
        private val LAST_ACTIVE_DATE_KEY = stringPreferencesKey("last_active_date")
        private val DATA_COLLECTION_CONSENT_KEY = booleanPreferencesKey("data_collection_consent")
        private val APP_THEME_KEY = stringPreferencesKey("app_theme")
        private val COMPLETED_ONBOARDING_KEY = booleanPreferencesKey("completed_onboarding")
    }

    /**
     * Obtém o usuário anônimo atual ou cria um novo se não existir.
     */
    override fun getAnonymousUser(): Flow<AnonymousUser> {
        return context.anonymousUserDataStore.data.map { preferences ->
            val deviceId = preferences[DEVICE_ID_KEY] ?: createAndSaveNewDeviceId()
            val preferredReminderTime = preferences[PREFERRED_REMINDER_TIME_KEY]
            val notificationsEnabled = preferences[NOTIFICATIONS_ENABLED_KEY] ?: true
            val lastActiveDate = preferences[LAST_ACTIVE_DATE_KEY]?.toLongOrNull() ?: System.currentTimeMillis()
            val dataConsent = preferences[DATA_COLLECTION_CONSENT_KEY] ?: false
            val appThemeStr = preferences[APP_THEME_KEY] ?: AppTheme.SYSTEM.name
            
            AnonymousUser(
                deviceId = deviceId,
                preferredReminderTime = preferredReminderTime,
                notificationsEnabled = notificationsEnabled,
                lastActiveDate = lastActiveDate,
                consentedToDataCollection = dataConsent,
                appTheme = try { AppTheme.valueOf(appThemeStr) } catch (e: Exception) { AppTheme.SYSTEM }
            )
        }
    }

    /**
     * Cria e salva um novo ID de dispositivo anônimo.
     */
    private suspend fun createAndSaveNewDeviceId(): String {
        val newId = UUID.randomUUID().toString()
        context.anonymousUserDataStore.edit { preferences ->
            preferences[DEVICE_ID_KEY] = newId
        }
        return newId
    }

    /**
     * Atualiza todos os dados do usuário anônimo.
     */
    override suspend fun updateAnonymousUser(user: AnonymousUser) {
        context.anonymousUserDataStore.edit { preferences ->
            preferences[DEVICE_ID_KEY] = user.deviceId
            preferences[PREFERRED_REMINDER_TIME_KEY] = user.preferredReminderTime ?: ""
            preferences[NOTIFICATIONS_ENABLED_KEY] = user.notificationsEnabled
            preferences[LAST_ACTIVE_DATE_KEY] = user.lastActiveDate.toString()
            preferences[DATA_COLLECTION_CONSENT_KEY] = user.consentedToDataCollection
            preferences[APP_THEME_KEY] = user.appTheme.name
        }
    }

    /**
     * Atualiza o consentimento para coleta de dados.
     */
    override suspend fun updateDataCollectionConsent(hasConsent: Boolean) {
        context.anonymousUserDataStore.edit { preferences ->
            preferences[DATA_COLLECTION_CONSENT_KEY] = hasConsent
        }
    }

    /**
     * Atualiza a preferência de notificações.
     */
    override suspend fun updateNotificationsEnabled(enabled: Boolean) {
        context.anonymousUserDataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED_KEY] = enabled
        }
    }

    /**
     * Atualiza o tema do aplicativo.
     */
    override suspend fun updateAppTheme(theme: AppTheme) {
        context.anonymousUserDataStore.edit { preferences ->
            preferences[APP_THEME_KEY] = theme.name
        }
    }

    /**
     * Atualiza o horário preferido para receber lembretes.
     */
    override suspend fun updatePreferredReminderTime(time: String?) {
        context.anonymousUserDataStore.edit { preferences ->
            preferences[PREFERRED_REMINDER_TIME_KEY] = time ?: ""
        }
    }

    /**
     * Gera um novo ID anônimo para o dispositivo.
     */
    override suspend fun generateNewAnonymousId(): AnonymousUser {
        val newId = UUID.randomUUID().toString()
        context.anonymousUserDataStore.edit { preferences ->
            preferences[DEVICE_ID_KEY] = newId
            // Mantém as outras preferências
        }
        
        return getAnonymousUser().map { user ->
            user.copy(deviceId = newId)
        }.firstOrNull() ?: AnonymousUser(deviceId = newId)
    }

    /**
     * Verifica se o usuário já completou o processo de onboarding.
     */
    override fun hasCompletedOnboarding(): Flow<Boolean> {
        return context.anonymousUserDataStore.data.map { preferences ->
            preferences[COMPLETED_ONBOARDING_KEY] ?: false
        }
    }

    /**
     * Marca o processo de onboarding como concluído.
     */
    override suspend fun markOnboardingCompleted() {
        context.anonymousUserDataStore.edit { preferences ->
            preferences[COMPLETED_ONBOARDING_KEY] = true
        }
    }
} 