package com.example.mindwell.app.data.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.mindwell.app.domain.entities.UserPreferences
import com.example.mindwell.app.domain.repositories.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

// Extensão para criar DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * Implementação do repositório para operações de preferências do usuário usando DataStore.
 */
@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {

    private object PreferencesKeys {
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val DATA_COLLECTION_CONSENT = booleanPreferencesKey("data_collection_consent")
        val LAST_ASSESSMENT_REMINDER = stringPreferencesKey("last_assessment_reminder")
    }

    override fun getUserPreferences(): Flow<UserPreferences> {
        return dataStore.data.map { preferences ->
            val notificationsEnabled = preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true
            val dataCollectionConsent = preferences[PreferencesKeys.DATA_COLLECTION_CONSENT] ?: false
            val lastReminderString = preferences[PreferencesKeys.LAST_ASSESSMENT_REMINDER]
            val lastReminder = lastReminderString?.let {
                LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            }
            
            UserPreferences(
                id = 1,
                notificationsEnabled = notificationsEnabled,
                dataCollectionConsent = dataCollectionConsent,
                lastAssessmentReminder = lastReminder
            )
        }
    }

    override suspend fun updateUserPreferences(preferences: UserPreferences): Boolean {
        return try {
            dataStore.edit { prefs ->
                prefs[PreferencesKeys.NOTIFICATIONS_ENABLED] = preferences.notificationsEnabled
                prefs[PreferencesKeys.DATA_COLLECTION_CONSENT] = preferences.dataCollectionConsent
                preferences.lastAssessmentReminder?.let {
                    prefs[PreferencesKeys.LAST_ASSESSMENT_REMINDER] = it.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }
} 