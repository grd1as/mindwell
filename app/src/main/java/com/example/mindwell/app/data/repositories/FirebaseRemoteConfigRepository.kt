package com.example.mindwell.app.data.repositories

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRemoteConfigRepository @Inject constructor() : RemoteConfigRepository {
    
    private val remote_config = Firebase.remoteConfig.apply {
        val config_settings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600 // 1 hour
        }
        setConfigSettingsAsync(config_settings)
        setDefaultsAsync(mapOf(
            APP_THEME to "light",
            WELCOME_MESSAGE to "Bem-vindo ao MindWell! Cuide da sua saúde mental.",
            FEATURE_METRICS_ENABLED to true,
            ASSESSMENT_FREQUENCY_DAYS to 7L
        ))
    }

    override suspend fun fetch_and_activate(): Boolean {
        return try {
            remote_config.fetchAndActivate().await()
        } catch (e: Exception) {
            false
        }
    }

    override fun get_app_theme(): Flow<String> = flow {
        emit(remote_config.getString(APP_THEME))
    }

    override fun get_welcome_message(): Flow<String> = flow {
        emit(remote_config.getString(WELCOME_MESSAGE))
    }

    override fun is_metrics_feature_enabled(): Flow<Boolean> = flow {
        emit(remote_config.getBoolean(FEATURE_METRICS_ENABLED))
    }

    override fun get_assessment_frequency_days(): Flow<Int> = flow {
        emit(remote_config.getLong(ASSESSMENT_FREQUENCY_DAYS).toInt())
    }

    companion object {
        private const val APP_THEME = "app_theme"
        private const val WELCOME_MESSAGE = "welcome_message"
        private const val FEATURE_METRICS_ENABLED = "feature_metrics_enabled"
        private const val ASSESSMENT_FREQUENCY_DAYS = "assessment_frequency_days"
    }
} 