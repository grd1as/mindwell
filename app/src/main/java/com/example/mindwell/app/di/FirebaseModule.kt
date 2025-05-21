package com.example.mindwell.app.di

import com.example.mindwell.app.data.repositories.FirebaseRemoteConfigRepository
import com.example.mindwell.app.data.repositories.RemoteConfigRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FirebaseModule {
    
    @Binds
    @Singleton
    abstract fun bind_remote_config_repository(
        repository: FirebaseRemoteConfigRepository
    ): RemoteConfigRepository
} 