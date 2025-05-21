package com.example.mindwell.app.domain.usecases.resource

import com.example.mindwell.app.data.repositories.RemoteConfigRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class Get_app_theme_use_case @Inject constructor(
    private val remote_config_repository: RemoteConfigRepository
) {
    operator fun invoke(): Flow<String> = remote_config_repository.get_app_theme()
}

class Get_welcome_message_use_case @Inject constructor(
    private val remote_config_repository: RemoteConfigRepository
) {
    operator fun invoke(): Flow<String> = remote_config_repository.get_welcome_message()
}

class Is_metrics_feature_enabled_use_case @Inject constructor(
    private val remote_config_repository: RemoteConfigRepository
) {
    operator fun invoke(): Flow<Boolean> = remote_config_repository.is_metrics_feature_enabled()
}

class Get_assessment_frequency_days_use_case @Inject constructor(
    private val remote_config_repository: RemoteConfigRepository
) {
    operator fun invoke(): Flow<Int> = remote_config_repository.get_assessment_frequency_days()
}

class Fetch_remote_config_use_case @Inject constructor(
    private val remote_config_repository: RemoteConfigRepository
) {
    suspend operator fun invoke(): Boolean = remote_config_repository.fetch_and_activate()
} 