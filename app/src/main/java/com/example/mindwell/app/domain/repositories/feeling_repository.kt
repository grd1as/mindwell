package com.example.mindwell.app.domain.repositories

import com.example.mindwell.app.domain.entities.Feeling

/**
 * Interface para o repositório de sentimentos.
 */
interface FeelingRepository {
    /**
     * Obtém a lista de sentimentos disponíveis para check-in.
     * @return Lista de sentimentos
     */
    suspend fun get_feelings(): List<Feeling>
} 