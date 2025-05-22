package com.example.mindwell.app.domain.usecases.checkin

import com.example.mindwell.app.domain.entities.Checkin
import com.example.mindwell.app.domain.repositories.CheckinRepository
import javax.inject.Inject

/**
 * Caso de uso para salvar um novo check-in.
 */
class SaveCheckinUseCase @Inject constructor(
    private val repository: CheckinRepository
) {
    /**
     * Invoca o caso de uso para salvar um check-in.
     * @param checkin O check-in a ser salvo
     * @return Resultado da operação com o ID do check-in ou um erro
     */
    suspend operator fun invoke(checkin: Checkin): Result<Long> {
        return repository.save_checkin(checkin)
    }
} 