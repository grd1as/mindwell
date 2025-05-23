package com.example.mindwell.app.di

import com.example.mindwell.app.data.services.GeminiService
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Módulo Hilt para serviços de AI (Gemini)
 * 
 * Nota: GeminiService usa @Inject constructor, então não precisa de @Provides
 */
@Module
@InstallIn(SingletonComponent::class)
object AiModule {
    // GeminiService é automaticamente fornecido pelo Hilt via @Inject constructor
} 