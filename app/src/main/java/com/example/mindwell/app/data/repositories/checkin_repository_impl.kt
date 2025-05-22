package com.example.mindwell.app.data.repositories

import com.example.mindwell.app.data.datasources.local.dao.CheckinDao
import com.example.mindwell.app.data.datasources.local.dao.EmotionDao
import com.example.mindwell.app.data.datasources.local.entities.CheckinEntity
import com.example.mindwell.app.data.model.CheckinDTO
import com.example.mindwell.app.data.network.ApiService
import com.example.mindwell.app.domain.entities.Checkin
import com.example.mindwell.app.domain.entities.CheckinPage
import com.example.mindwell.app.domain.entities.Emotion
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
        // Para manter compatibilidade com a versão atual da interface,
        // implementamos uma lógica que busca do banco local e simula paginação
        
        val start_date = from ?: LocalDate.now().minusMonths(1)
        val end_date = to ?: LocalDate.now()
        
        // Busca todos os check-ins no período e depois pagina na memória
        val all_checkins = get_checkins_by_date_range(start_date, end_date)
            .map { list -> list.sortedByDescending { it.date } }
            .firstOrNull() ?: emptyList()
        
        val start_index = page * size
        val end_index = minOf(start_index + size, all_checkins.size)
        
        val items = if (start_index < all_checkins.size) {
            all_checkins.subList(start_index, end_index)
        } else {
            emptyList()
        }
        
        val total_pages = (all_checkins.size + size - 1) / size
        
        return CheckinPage(
            page = page,
            size = size,
            total_pages = total_pages,
            total_items = all_checkins.size,
            items = items,
            current_page = page,
            total_elements = all_checkins.size
        )
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