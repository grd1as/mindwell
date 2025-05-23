package com.example.mindwell.app.data.repositories

import com.example.mindwell.app.data.datasources.local.dao.CheckinDao
import com.example.mindwell.app.data.datasources.local.dao.EmotionDao
import com.example.mindwell.app.data.datasources.local.entities.CheckinEntity
import com.example.mindwell.app.data.model.CheckinDTO
import com.example.mindwell.app.data.network.ApiService
import com.example.mindwell.app.domain.entities.Checkin
import com.example.mindwell.app.domain.entities.CheckinPage
import com.example.mindwell.app.domain.entities.Emotion
import com.example.mindwell.app.domain.entities.MonthlySummary
import com.example.mindwell.app.domain.entities.OptionCount
import com.example.mindwell.app.domain.entities.WorkloadInfo
import com.example.mindwell.app.domain.repositories.CheckinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

/**
 * Implementação do repositório de check-ins usando banco de dados local.
 */
class CheckinRepositoryImpl @Inject constructor(
    private val checkin_dao: CheckinDao,
    private val emotion_dao: EmotionDao,
    private val api_service: ApiService
) : CheckinRepository {
    
    override fun get_checkins(): Flow<List<Checkin>> {
        return checkin_dao.getAllCheckins().map { checkin_entities ->
            checkin_entities.map { entity ->
                map_entity_to_domain(entity)
            }
        }
    }
    
    override fun get_checkins_by_date_range(
        start_date: LocalDate,
        end_date: LocalDate
    ): Flow<List<Checkin>> {
        val start_date_time = LocalDateTime.of(start_date, LocalTime.MIN)
        val end_date_time = LocalDateTime.of(end_date, LocalTime.MAX)
        
        return checkin_dao.getCheckinsByDateRange(start_date_time, end_date_time).map { checkin_entities ->
            checkin_entities.map { entity ->
                map_entity_to_domain(entity)
            }
        }
    }
    
    override suspend fun save_checkin(checkin: Checkin): Result<Long> {
        return try {
            // Salva localmente
            val entity = map_domain_to_entity(checkin)
            val local_id = checkin_dao.insertCheckin(entity)
            
            // Tenta enviar para a API (implementação futura)
            // Para o exemplo, apenas simula o envio
            
            Result.success(local_id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun get_last_checkin(): Flow<Checkin?> {
        return checkin_dao.getLastCheckin().map { entity ->
            entity?.let { map_entity_to_domain(it) }
        }
    }
    
    override suspend fun get_checkins(
        page: Int,
        size: Int,
        from: LocalDate?,
        to: LocalDate?
    ): CheckinPage {
        // Forçar uso da API real - sem fallback para local
        try {
            // Tenta obter dados da API
            val response = api_service.get_checkins(page, size, from?.toString(), to?.toString())
            return CheckinPage(
                page = response.page,
                size = response.size,
                total_pages = response.total_pages,
                total_items = response.total_items,
                items = response.items.map { dto ->
                    // Tenta extrair a emoção das respostas (assumindo que a primeira resposta contém a emoção)
                    val emotionAnswer = dto.answers.firstOrNull()
                    val emotionValue = emotionAnswer?.value?.toIntOrNull() ?: 3 // Valor padrão se não encontrar
                    
                    // Mapeamento baseado no valor da emoção
                    val emotion = when (emotionValue) {
                        1 -> Emotion(id = 1, name = "Muito mal", emoji = "😭", value = 1)
                        2 -> Emotion(id = 2, name = "Mal", emoji = "😢", value = 2)
                        3 -> Emotion(id = 3, name = "Normal", emoji = "😐", value = 3)
                        4 -> Emotion(id = 4, name = "Bem", emoji = "🙂", value = 4)
                        5 -> Emotion(id = 5, name = "Muito bem", emoji = "😄", value = 5)
                        else -> Emotion(id = 3, name = "Normal", emoji = "😐", value = 3)
                    }
                    
                    Checkin(
                        id = dto.checkin_id.toLong(),
                        date = dto.timestamp,
                        emotion = emotion,
                        note = null, // Não disponível no DTO atual
                        streak = dto.streak
                    )
                },
                current_page = response.page,
                total_elements = response.total_items // Usando total_items como total_elements
            )
        } catch (e: Exception) {
            throw e // Propaga o erro em vez de usar fallback
        }
    }
    
    override suspend fun get_monthly_summary(year: Int, month: Int): MonthlySummary {
        try {
            // Chama a API do resumo mensal
            val response = api_service.get_monthly_summary(year, month)
            
            return MonthlySummary(
                period = response.period,
                total_checkins = response.totalCheckins,
                predominant_emoji = response.predominantEmoji.map { 
                    OptionCount(
                        option_id = it.optionId,
                        label = it.label,
                        count = it.count
                    )
                },
                predominant_sentiment = response.predominantSentiment.map {
                    OptionCount(
                        option_id = it.optionId,
                        label = it.label,
                        count = it.count
                    )
                },
                trend = response.trend,
                workload = WorkloadInfo(
                    current_avg = response.workload.currentAvg,
                    previous_avg = response.workload.previousAvg,
                    percent_change = response.workload.percentChange
                )
            )
        } catch (e: Exception) {
            throw e
        }
    }
    
    private suspend fun map_entity_to_domain(entity: CheckinEntity): Checkin {
        val emotion_entity = emotion_dao.getEmotionById(entity.emotionId)
        val emotion = Emotion(
            id = emotion_entity?.id ?: 0,
            name = emotion_entity?.name ?: "Desconhecido",
            emoji = emotion_entity?.emoji ?: "❓",
            value = emotion_entity?.value ?: 0
        )
        
        return Checkin(
            id = entity.id,
            date = entity.answeredAt.toLocalDate().toString(),
            emotion = emotion,
            note = entity.note,
            streak = entity.streak
        )
    }
    
    private fun map_domain_to_entity(domain: Checkin): CheckinEntity {
        return CheckinEntity(
            id = domain.id,
            answeredAt = LocalDateTime.now(),
            emotionId = domain.emotion.id,
            streak = domain.streak ?: 0,
            note = domain.note
        )
    }
} 