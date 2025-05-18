package com.example.mindwell.app.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Módulo principal do aplicativo para injeção de dependências com Hilt.
 * Este módulo contém dependências no escopo de aplicação.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // Aqui podemos adicionar outras dependências no escopo da aplicação
    // que não estejam diretamente relacionadas a dados
} 