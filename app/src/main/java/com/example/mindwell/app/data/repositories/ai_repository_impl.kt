package com.example.mindwell.app.data.repositories

import com.example.mindwell.app.data.services.GeminiService
import com.example.mindwell.app.data.services.PersonalizedContentResponse
import com.example.mindwell.app.data.services.PersonalizedTip
import com.example.mindwell.app.data.services.UserProfileData
import com.example.mindwell.app.domain.entities.Checkin
import com.example.mindwell.app.domain.repositories.AiRepository
import com.example.mindwell.app.domain.repositories.CheckinRepository
import com.example.mindwell.app.domain.repositories.PreferenceRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log

/**
 * Implementação do repositório de AI (Gemini)
 */
@Singleton
class AiRepositoryImpl @Inject constructor(
    private val gemini_service: GeminiService,
    private val checkin_repository: CheckinRepository,
    private val preference_repository: PreferenceRepository
) : AiRepository {

    override suspend fun generate_personalized_resources(user_data: UserProfileData): Result<PersonalizedContentResponse> {
        return gemini_service.generate_personalized_resources(user_data)
    }

    override suspend fun generate_personalized_tips(user_data: UserProfileData): Result<List<PersonalizedTip>> {
        return gemini_service.generate_personalized_tips(user_data)
    }

    override suspend fun get_user_profile_data(): UserProfileData {
        return try {
            // Obter dados de check-ins recentes (últimos 7 dias)
            val end_date = LocalDate.now()
            val start_date = end_date.minusDays(7)
            
            val recent_checkins = checkin_repository.get_checkins_by_date_range(
                start_date = start_date,
                end_date = end_date
            ).first()

            Log.d("AiRepository", "📊 Analisando ${recent_checkins.size} check-ins dos últimos 7 dias")

            // Extrair sentimentos e padrões
            val recent_feelings = recent_checkins.map { it.emotion.name }.distinct()
            val emotion_values = recent_checkins.map { it.emotion.value }
            val average_emotion = if (emotion_values.isNotEmpty()) emotion_values.average() else 5.0

            // Calcular níveis baseados nos check-ins
            val stress_level = calculate_stress_level(recent_checkins)
            val anxiety_level = calculate_anxiety_level(recent_checkins)
            val energy_level = calculate_energy_level(recent_checkins)

            // Obter preferências do usuário
            val user_preferences = try {
                // preference_repository.get_user_preferences().first()
                emptyList<String>() // Placeholder por enquanto
            } catch (e: Exception) {
                emptyList<String>()
            }

            // Dados temporais e contextuais
            val now = LocalDateTime.now()
            val current_time = now.format(DateTimeFormatter.ofPattern("HH:mm"))
            val day_of_week = now.dayOfWeek.getDisplayName(
                java.time.format.TextStyle.FULL,
                Locale.getDefault()
            )
            
            // Análise de contexto temporal
            val time_context = getTimeContext(now.hour)
            val enhanced_preferences = getEnhancedPreferences(recent_checkins, now.hour)

            // Construir perfil enriquecido
            val profile = UserProfileData(
                recent_feelings = recent_feelings,
                stress_level = stress_level,
                anxiety_level = anxiety_level,
                energy_level = energy_level,
                sleep_pattern = determine_sleep_pattern(recent_checkins),
                preferred_activities = enhanced_preferences,
                current_time = "$current_time ($time_context)",
                day_of_week = day_of_week,
                current_mood = determine_current_mood(average_emotion),
                mood_history = create_mood_history(recent_checkins),
                last_checkin_time = recent_checkins.firstOrNull()?.date ?: "Sem check-ins recentes",
                patterns = identify_patterns(recent_checkins)
            )
            
            Log.d("AiRepository", "✅ Perfil do usuário criado: ${profile.current_mood}, stress: ${profile.stress_level}, energia: ${profile.energy_level}")
            profile
            
        } catch (e: Exception) {
            Log.e("AiRepository", "❌ Erro ao criar perfil do usuário: ${e.message}")
            // Retornar dados padrão em caso de erro
            get_default_user_profile_data()
        }
    }

    private fun calculate_stress_level(checkins: List<Checkin>): Int {
        if (checkins.isEmpty()) return 5
        
        // Calcular baseado em sentimentos negativos e scores baixos
        val negative_feelings = checkins.count { checkin ->
            checkin.emotion.name.lowercase() in listOf("ansioso", "estressado", "triste", "irritado", "sobrecarregado")
        }
        val avg_emotion = checkins.map { it.emotion.value }.average()
        
        return when {
            negative_feelings >= checkins.size * 0.7 || avg_emotion <= 3 -> 8
            negative_feelings >= checkins.size * 0.5 || avg_emotion <= 4 -> 6
            negative_feelings >= checkins.size * 0.3 || avg_emotion <= 5 -> 4
            else -> 2
        }.coerceIn(1, 10)
    }

    private fun calculate_anxiety_level(checkins: List<Checkin>): Int {
        if (checkins.isEmpty()) return 5

        val anxiety_feelings = checkins.count { checkin ->
            checkin.emotion.name.lowercase() in listOf("ansioso", "nervoso", "preocupado", "inquieto")
        }
        
        return when {
            anxiety_feelings >= checkins.size * 0.6 -> 8
            anxiety_feelings >= checkins.size * 0.4 -> 6
            anxiety_feelings >= checkins.size * 0.2 -> 4
            else -> 2
        }.coerceIn(1, 10)
    }

    private fun calculate_energy_level(checkins: List<Checkin>): Int {
        if (checkins.isEmpty()) return 5

        val energy_feelings = checkins.count { checkin ->
            checkin.emotion.name.lowercase() in listOf("energizado", "motivado", "produtivo", "ativo")
        }
        val low_energy = checkins.count { checkin ->
            checkin.emotion.name.lowercase() in listOf("cansado", "exausto", "apático", "letárgico")
        }
        
        return when {
            energy_feelings >= checkins.size * 0.5 -> 8
            energy_feelings >= checkins.size * 0.3 -> 6
            low_energy >= checkins.size * 0.5 -> 2
            low_energy >= checkins.size * 0.3 -> 4
            else -> 5
        }.coerceIn(1, 10)
    }

    private fun determine_sleep_pattern(checkins: List<Checkin>): String {
        val sleep_related = checkins.count { checkin ->
            checkin.emotion.name.lowercase() in listOf("cansado", "exausto", "sonolento")
        }
        
        return when {
            sleep_related >= checkins.size * 0.5 -> "irregular"
            sleep_related >= checkins.size * 0.3 -> "parcialmente_irregular"
            else -> "normal"
        }
    }

    private fun determine_current_mood(average_score: Double): String {
        return when {
            average_score >= 8 -> "muito_positivo"
            average_score >= 6 -> "positivo"
            average_score >= 4 -> "neutro"
            average_score >= 2 -> "negativo"
            else -> "muito_negativo"
        }
    }

    private fun create_mood_history(checkins: List<Checkin>): String {
        if (checkins.isEmpty()) return "Sem histórico disponível"
        
        val trend = if (checkins.size >= 2) {
            val recent_avg = checkins.take(3).map { it.emotion.value }.average()
            val older_avg = checkins.drop(3).take(4).map { it.emotion.value }.average()
            
            when {
                recent_avg > older_avg + 1 -> "em melhora"
                recent_avg < older_avg - 1 -> "em declínio"
                else -> "estável"
            }
        } else "insuficiente"
        
        return "Tendência $trend nos últimos 7 dias"
    }

    private fun identify_patterns(checkins: List<Checkin>): List<String> {
        val patterns = mutableListOf<String>()
        
        if (checkins.isEmpty()) return patterns
        
        // Padrão de consistência
        if (checkins.size >= 5) {
            patterns.add("Usuário consistente com check-ins")
        }
        
        // Padrão de variação de humor
        val emotion_variance = checkins.map { it.emotion.value }.let { scores ->
            if (scores.size > 1) {
                val avg = scores.average()
                scores.map { (it - avg) * (it - avg) }.average()
            } else 0.0
        }
        
        when {
            emotion_variance > 4 -> patterns.add("Humor variável")
            emotion_variance < 1 -> patterns.add("Humor estável")
        }
        
        // Padrão de emoções predominantes
        val emotion_groups = checkins.groupBy { it.emotion.name }
        val most_common = emotion_groups.maxByOrNull { it.value.size }?.key
        
        most_common?.let { 
            if (emotion_groups[it]?.size ?: 0 >= checkins.size * 0.4) {
                patterns.add("Frequentemente sente: $it")
            }
        }
        
        return patterns
    }

    private fun get_default_user_profile_data(): UserProfileData {
        val now = LocalDateTime.now()
        return UserProfileData(
            recent_feelings = listOf("neutro"),
            current_time = now.format(DateTimeFormatter.ofPattern("HH:mm")),
            day_of_week = now.dayOfWeek.getDisplayName(
                java.time.format.TextStyle.FULL,
                Locale.getDefault()
            ),
            current_mood = "neutro"
        )
    }

    /**
     * Determina o contexto temporal para melhorar as recomendações
     */
    private fun getTimeContext(hour: Int): String {
        return when (hour) {
            in 5..11 -> "manhã - momento ideal para energizar"
            in 12..17 -> "tarde - período de foco e produtividade"
            in 18..22 -> "noite - hora de relaxar e refletir"
            else -> "madrugada - momento para descanso"
        }
    }
    
    /**
     * Gera preferências baseadas em padrões e contexto temporal
     */
    private fun getEnhancedPreferences(checkins: List<Checkin>, hour: Int): List<String> {
        val preferences = mutableListOf<String>()
        
        // Baseado no horário
        when (hour) {
            in 6..10 -> preferences.addAll(listOf("exercícios matinais", "meditação", "planejamento do dia"))
            in 11..14 -> preferences.addAll(listOf("exercícios de respiração", "pausa para reflexão"))
            in 15..18 -> preferences.addAll(listOf("atividade física", "mindfulness", "pausas ativas"))
            in 19..22 -> preferences.addAll(listOf("relaxamento", "journaling", "gratidão", "preparação para sono"))
            else -> preferences.addAll(listOf("relaxamento profundo", "meditação para sono"))
        }
        
        // Baseado em padrões emocionais
        if (checkins.isNotEmpty()) {
            val dominant_emotions = checkins.groupBy { it.emotion.name }
                .maxByOrNull { it.value.size }?.key
                
            when (dominant_emotions?.lowercase()) {
                "ansioso", "nervoso" -> preferences.add("técnicas de grounding")
                "cansado", "exausto" -> preferences.add("atividades restauradoras")
                "motivado", "energizado" -> preferences.add("desafios pessoais")
                "triste" -> preferences.add("práticas de autocompaixão")
            }
        }
        
        return preferences.distinct()
    }
} 