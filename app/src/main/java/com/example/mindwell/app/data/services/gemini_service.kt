package com.example.mindwell.app.data.services

import android.content.Context
import android.util.Log
import com.example.mindwell.app.R
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Serviço para integração com Gemini AI
 */
@Singleton
class GeminiService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val TAG = "GeminiService"
    private val gson = Gson()
    
    private val api_key = context.getString(R.string.gemini_api_key)
    
    private val generative_model = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = api_key
    )
    
    /**
     * Gera conteúdo personalizado de recursos baseado no perfil do usuário
     */
    suspend fun generate_personalized_resources(user_data: UserProfileData): Result<PersonalizedContentResponse> {
        return try {
            Log.d(TAG, "🤖 Gerando recursos personalizados para: ${user_data.current_mood}")
            
            val prompt = build_resources_prompt(user_data)
            
            val response = generative_model.generateContent(
                content {
                    text(prompt)
                }
            )
            
            val content = response.text ?: throw Exception("Resposta vazia do Gemini")
            Log.d(TAG, "📝 Resposta do Gemini recebida: ${content.take(200)}...")
            
            // Analisar qualidade da resposta
            GeminiDebugUtils.analyzeResponse(content, "resources")
            
            val parsed_response = parse_resources_response(content)
            
            Log.d(TAG, "✅ Recursos personalizados gerados com sucesso: ${parsed_response.resources.size} recursos")
            Result.success(parsed_response)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro ao gerar recursos personalizados: ${e.message}", e)
            
            // Fallback para dados mock
            Log.d(TAG, "🔄 Usando dados mock como fallback")
            val mock_response = PersonalizedContentResponse(
                resources = getMockResources(),
                personalized_message = "Baseado no seu perfil, selecionei estes recursos especialmente para você hoje. (Modo offline)"
            )
            Result.success(mock_response)
        }
    }
    
    /**
     * Gera dicas personalizadas baseadas no estado emocional atual
     */
    suspend fun generate_personalized_tips(user_data: UserProfileData): Result<List<PersonalizedTip>> {
        return try {
            Log.d(TAG, "💡 Gerando dicas personalizadas para nível de ansiedade: ${user_data.anxiety_level}")
            
            val prompt = build_tips_prompt(user_data)
            
            val response = generative_model.generateContent(
                content {
                    text(prompt)
                }
            )
            
            val content = response.text ?: throw Exception("Resposta vazia do Gemini")
            Log.d(TAG, "📝 Resposta de dicas recebida: ${content.take(150)}...")
            
            // Analisar qualidade da resposta
            GeminiDebugUtils.analyzeResponse(content, "tips")
            
            val tips = parse_tips_response(content)
            
            Log.d(TAG, "✅ Dicas personalizadas geradas com sucesso: ${tips.size} dicas")
            Result.success(tips)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro ao gerar dicas personalizadas: ${e.message}", e)
            
            // Fallback para dados mock
            Log.d(TAG, "🔄 Usando dicas mock como fallback")
            Result.success(getMockTips())
        }
    }
    
    private fun build_resources_prompt(user_data: UserProfileData): String {
        return """
            Você é um assistente especializado em saúde mental e bem-estar. Com base no perfil do usuário abaixo, gere recursos personalizados de bem-estar.
            
            PERFIL DO USUÁRIO:
            - Sentimentos recentes: ${user_data.recent_feelings.joinToString(", ")}
            - Nível de estresse: ${user_data.stress_level}/10
            - Nível de ansiedade: ${user_data.anxiety_level}/10
            - Nível de energia: ${user_data.energy_level}/10
            - Padrões de sono: ${user_data.sleep_pattern}
            - Atividades preferidas: ${user_data.preferred_activities.joinToString(", ")}
            - Horário atual: ${user_data.current_time}
            - Dia da semana: ${user_data.day_of_week}
            - Estado atual: ${user_data.current_mood}
            - Histórico de humor: ${user_data.mood_history}
            - Padrões identificados: ${user_data.patterns.joinToString(", ")}
            
            Por favor, gere 4-6 recursos personalizados no seguinte formato JSON EXATO:
            
            {
              "resources": [
                {
                  "title": "Título claro e motivador",
                  "description": "Descrição específica e útil (máximo 100 caracteres)",
                  "category": "breathing",
                  "duration_minutes": 5,
                  "difficulty": "beginner",
                  "icon": "breathing",
                  "action_text": "Começar agora"
                }
              ],
              "personalized_message": "Mensagem motivadora personalizada (máximo 150 caracteres)"
            }
            
            CATEGORIAS VÁLIDAS: breathing, meditation, exercise, sleep, journaling, mindfulness
            DIFICULDADES VÁLIDAS: beginner, intermediate, advanced
            ÍCONES VÁLIDOS: breathing, meditation, exercise, sleep, writing, heart
            
            DIRETRIZES IMPORTANTES:
            - Responda APENAS com JSON válido (sem explicações antes ou depois)
            - Adapte as recomendações ao estado emocional atual
            - Priorize atividades apropriadas para o horário (${user_data.current_time})
            - Use linguagem acolhedora e motivadora em português brasileiro
            - Mantenha descriptions e messages concisos
        """.trimIndent()
    }
    
    private fun build_tips_prompt(user_data: UserProfileData): String {
        return """
            Você é um psicólogo especializado em bem-estar digital. Baseado no perfil emocional do usuário, gere 3-4 dicas personalizadas e práticas.
            
            PERFIL EMOCIONAL:
            - Estado atual: ${user_data.current_mood}
            - Nível de ansiedade: ${user_data.anxiety_level}/10
            - Nível de estresse: ${user_data.stress_level}/10
            - Energia: ${user_data.energy_level}/10
            - Último check-in: ${user_data.last_checkin_time}
            - Padrões identificados: ${user_data.patterns.joinToString(", ")}
            - Horário: ${user_data.current_time} (${user_data.day_of_week})
            
            Gere dicas no formato JSON EXATO:
            
            {
              "tips": [
                {
                  "title": "Título da dica (máximo 50 caracteres)",
                  "content": "Conteúdo detalhado e prático (máximo 200 caracteres)",
                  "category": "immediate",
                  "icon": "lightbulb",
                  "estimated_time": "2 minutos",
                  "priority": "high"
                }
              ]
            }
            
            CATEGORIAS VÁLIDAS: immediate, breathing, movement, mindset, planning
            ÍCONES VÁLIDOS: lightbulb, heart, star, smile, target
            PRIORIDADES VÁLIDAS: high, medium, low
            
            DIRETRIZES:
            - Responda APENAS com JSON válido (sem texto adicional)
            - Dicas práticas e aplicáveis imediatamente
            - Linguagem empática e encorajadora
            - Considere o contexto temporal (${user_data.current_time})
            - Mantenha textos concisos
        """.trimIndent()
    }
    
    private fun parse_resources_response(content: String): PersonalizedContentResponse {
        return try {
            Log.d(TAG, "🔄 Fazendo parsing da resposta de recursos...")
            
            // Limpar a resposta removendo possíveis caracteres extras
            val cleanedContent = extractJsonFromResponse(content)
            Log.d(TAG, "🧹 JSON limpo: $cleanedContent")
            
            // Parse da resposta do Gemini
            val geminiResponse = gson.fromJson(cleanedContent, GeminiResourcesResponse::class.java)
            
            // Converter para formato interno
            val personalizedResources = geminiResponse.resources.map { item ->
                PersonalizedResource(
                    title = item.title,
                    description = item.description,
                    category = item.category,
                    duration_minutes = item.duration_minutes,
                    difficulty = item.difficulty,
                    icon = item.icon,
                    action_text = item.action_text
                )
            }
            
            Log.d(TAG, "✅ Parsing de recursos concluído com sucesso")
            PersonalizedContentResponse(
                resources = personalizedResources,
                personalized_message = geminiResponse.personalized_message
            )
            
        } catch (e: JsonSyntaxException) {
            Log.e(TAG, "❌ Erro de sintaxe JSON: ${e.message}")
            Log.e(TAG, "📄 Conteúdo que causou erro: $content")
            throw Exception("Formato JSON inválido na resposta do Gemini")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro geral no parsing: ${e.message}")
            throw e
        }
    }
    
    private fun parse_tips_response(content: String): List<PersonalizedTip> {
        return try {
            Log.d(TAG, "🔄 Fazendo parsing da resposta de dicas...")
            
            // Limpar a resposta
            val cleanedContent = extractJsonFromResponse(content)
            Log.d(TAG, "🧹 JSON de dicas limpo: $cleanedContent")
            
            // Parse da resposta do Gemini
            val geminiResponse = gson.fromJson(cleanedContent, GeminiTipsResponse::class.java)
            
            // Converter para formato interno
            val personalizedTips = geminiResponse.tips.map { item ->
                PersonalizedTip(
                    title = item.title,
                    content = item.content,
                    category = item.category,
                    icon = item.icon,
                    estimated_time = item.estimated_time,
                    priority = item.priority
                )
            }
            
            Log.d(TAG, "✅ Parsing de dicas concluído com sucesso")
            personalizedTips
            
        } catch (e: JsonSyntaxException) {
            Log.e(TAG, "❌ Erro de sintaxe JSON nas dicas: ${e.message}")
            throw Exception("Formato JSON inválido na resposta de dicas do Gemini")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro geral no parsing de dicas: ${e.message}")
            throw e
        }
    }
    
    /**
     * Extrai JSON válido da resposta do Gemini, removendo texto explicativo
     */
    private fun extractJsonFromResponse(content: String): String {
        return try {
            // Procurar pelo primeiro { e último }
            val startIndex = content.indexOf("{")
            val endIndex = content.lastIndexOf("}") + 1
            
            if (startIndex != -1 && endIndex > startIndex) {
                val jsonContent = content.substring(startIndex, endIndex)
                Log.d(TAG, "🎯 JSON extraído com sucesso")
                jsonContent
            } else {
                Log.w(TAG, "⚠️ Não foi possível extrair JSON, usando conteúdo original")
                content
            }
        } catch (e: Exception) {
            Log.w(TAG, "⚠️ Erro ao extrair JSON: ${e.message}, usando conteúdo original")
            content
        }
    }
    
    private fun getMockResources(): List<PersonalizedResource> {
        return listOf(
            PersonalizedResource(
                title = "Respiração Calmante de 5 Minutos",
                description = "Técnica de respiração 4-7-8 para reduzir a ansiedade e promover relaxamento",
                category = "breathing",
                duration_minutes = 5,
                difficulty = "beginner",
                icon = "breathing",
                action_text = "Começar agora"
            ),
            PersonalizedResource(
                title = "Meditação Guiada para Foco",
                description = "Sessão de mindfulness para melhorar concentração e clareza mental",
                category = "meditation",
                duration_minutes = 10,
                difficulty = "intermediate",
                icon = "meditation",
                action_text = "Meditar"
            ),
            PersonalizedResource(
                title = "Diário de Gratidão",
                description = "Reflita sobre três coisas pelas quais você é grato hoje",
                category = "journaling",
                duration_minutes = 8,
                difficulty = "beginner",
                icon = "writing",
                action_text = "Escrever"
            )
        )
    }
    
    private fun getMockTips(): List<PersonalizedTip> {
        return listOf(
            PersonalizedTip(
                title = "Momento de Pausa Consciente",
                content = "Faça três respirações profundas e observe como você está se sentindo neste momento, sem julgamentos.",
                category = "immediate",
                icon = "lightbulb",
                estimated_time = "2 minutos",
                priority = "high"
            ),
            PersonalizedTip(
                title = "Hidratação Mindful",
                content = "Beba um copo d'água lentamente, prestando atenção na sensação e temperatura. Isso ajuda a reconectar com o presente.",
                category = "immediate",
                icon = "heart",
                estimated_time = "3 minutos",
                priority = "medium"
            )
        )
    }
}

/**
 * Dados do perfil do usuário para personalização
 */
data class UserProfileData(
    val recent_feelings: List<String> = emptyList(),
    val stress_level: Int = 5,
    val anxiety_level: Int = 5,
    val energy_level: Int = 5,
    val sleep_pattern: String = "normal",
    val preferred_activities: List<String> = emptyList(),
    val current_time: String = "",
    val day_of_week: String = "",
    val current_mood: String = "neutro",
    val mood_history: String = "",
    val last_checkin_time: String = "",
    val patterns: List<String> = emptyList()
)

/**
 * Resposta com conteúdo personalizado
 */
data class PersonalizedContentResponse(
    val resources: List<PersonalizedResource>,
    val personalized_message: String
)

/**
 * Recurso personalizado gerado pelo Gemini
 */
data class PersonalizedResource(
    val title: String,
    val description: String,
    val category: String,
    val duration_minutes: Int,
    val difficulty: String,
    val icon: String,
    val action_text: String
)

/**
 * Dica personalizada gerada pelo Gemini
 */
data class PersonalizedTip(
    val title: String,
    val content: String,
    val category: String,
    val icon: String,
    val estimated_time: String,
    val priority: String
) 