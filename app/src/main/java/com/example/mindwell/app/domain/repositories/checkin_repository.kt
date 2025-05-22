package com.example.mindwell.app.domain.repositories

import com.example.mindwell.app.domain.entities.CheckinPage
import java.time.LocalDate

/**
 * Interface para o repositório de check-ins.
 */
interface CheckinRepository {
    /**
     * Obtém os check-ins do usuário com paginação e filtro.
     * @param page Número da página (padrão 0)
     * @param size Tamanho da página (padrão 20)
     * @param from Data inicial para filtro (opcional)
     * @param to Data final para filtro (opcional)
     * @return Página de check-ins
     */
    suspend fun getCheckins(
        page: Int = 0,
        size: Int = 20,
        from: LocalDate? = null,
        to: LocalDate? = null
    ): CheckinPage
} 