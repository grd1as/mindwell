package com.example.mindwell.app.domain.repositories

import com.example.mindwell.app.domain.entities.Checkin
import com.example.mindwell.app.domain.entities.CheckinPage
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Interface para o repositório de check-ins.
 */
interface CheckinRepository {
    /**
     * Obtém todos os check-ins salvos.
     * @return Flow de lista de check-ins
     */
    fun get_checkins(): Flow<List<Checkin>>
    
    /**
     * Obtém check-ins dentro de um intervalo de datas.
     * @param start_date Data inicial
     * @param end_date Data final
     * @return Flow de lista de check-ins
     */
    fun get_checkins_by_date_range(
        start_date: LocalDate,
        end_date: LocalDate
    ): Flow<List<Checkin>>
    
    /**
     * Salva um novo check-in.
     * @param checkin Check-in a ser salvo
     * @return Resultado com id do check-in salvo ou erro
     */
    suspend fun save_checkin(checkin: Checkin): Result<Long>
    
    /**
     * Obtém o último check-in realizado.
     * @return Flow de check-in opcional
     */
    fun get_last_checkin(): Flow<Checkin?>

    /**
     * Obtém os check-ins do usuário com paginação e filtro.
     * @param page Número da página (padrão 0)
     * @param size Tamanho da página (padrão 20)
     * @param from Data inicial para filtro (opcional)
     * @param to Data final para filtro (opcional)
     * @return Página de check-ins
     */
    suspend fun get_checkins(
        page: Int = 0,
        size: Int = 20,
        from: LocalDate? = null,
        to: LocalDate? = null
    ): CheckinPage
} 