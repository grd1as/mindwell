package com.example.mindwell.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Classe de aplicação principal do MindWell.
 * A anotação [HiltAndroidApp] inicializa o Hilt para injeção de dependência.
 */
@HiltAndroidApp
class MindWellApplication : Application() 