package com.example.mindwell.app.data.repositories

import kotlinx.coroutines.flow.Flow

interface RemoteConfigRepository {
    suspend fun fetch_and_activate(): Boolean
    fun get_app_theme(): Flow<String>
    fun get_welcome_message(): Flow<String>
    fun is_metrics_feature_enabled(): Flow<Boolean>
    fun get_assessment_frequency_days(): Flow<Int>
} 