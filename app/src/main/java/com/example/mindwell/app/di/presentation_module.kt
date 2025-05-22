package com.example.mindwell.app.di

import android.content.Context
import com.example.mindwell.app.presentation.screens.login.GoogleSignInClientFactory
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PresentationModule {
    
    // ID do cliente OAuth para autenticação com Google
    private const val SERVER_CLIENT_ID = "YOUR_OAUTH_CLIENT_ID"
    
    @Provides
    @Singleton
    fun provide_google_sign_in_client(
        factory: GoogleSignInClientFactory
    ): GoogleSignInClient {
        return factory.create(SERVER_CLIENT_ID)
    }
} 