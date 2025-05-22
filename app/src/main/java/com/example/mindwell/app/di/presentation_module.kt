package com.example.mindwell.app.di

import android.content.Context
import com.example.mindwell.app.R
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
    
    @Provides
    @Singleton
    fun provide_google_sign_in_client(
        factory: GoogleSignInClientFactory,
        @ApplicationContext context: Context
    ): GoogleSignInClient {
        val clientId = context.getString(R.string.web_client_id)
        return factory.create(clientId)
    }
} 