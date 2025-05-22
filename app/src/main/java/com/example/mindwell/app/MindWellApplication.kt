package com.example.mindwell.app

import android.app.Application
import com.example.mindwell.app.data.datasources.local.database.DatabaseInitializer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Classe de aplicação principal do MindWell.
 * A anotação [HiltAndroidApp] inicializa o Hilt para injeção de dependência.
 */
@HiltAndroidApp
class MindWellApplication : Application() {
    
    @Inject
    lateinit var databaseInitializer: DatabaseInitializer
    
    override fun onCreate() {
        super.onCreate()
        
        // Inicializa dados no banco de dados
        databaseInitializer.initialize()
    }
} 