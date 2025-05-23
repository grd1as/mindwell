package com.example.mindwell.app.data.datasources.local.database

import com.example.mindwell.app.data.datasources.local.dao.EmotionDao
import com.example.mindwell.app.data.datasources.local.entities.EmotionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Inicializador do banco de dados, responsável por inserir dados iniciais.
 */
class DatabaseInitializer @Inject constructor(
    private val emotion_dao: EmotionDao
) {
    /**
     * Inicializa o banco de dados com dados pré-configurados.
     */
    fun initialize() {
        CoroutineScope(Dispatchers.IO).launch {
            insert_default_emotions()
        }
    }
    
    private suspend fun insert_default_emotions() {
        // Emoções padrão para inicialização do banco local (não são dados da API)
        val default_emotions = listOf(
            EmotionEntity(id = 1, name = "Muito mal", emoji = "😭", value = 1),
            EmotionEntity(id = 2, name = "Mal", emoji = "😢", value = 2),
            EmotionEntity(id = 3, name = "Regular", emoji = "😐", value = 3),
            EmotionEntity(id = 4, name = "Bem", emoji = "🙂", value = 4),
            EmotionEntity(id = 5, name = "Muito bem", emoji = "😄", value = 5)
        )
        
        emotion_dao.insertEmotions(default_emotions)
    }
} 