package com.example.mindwell.app.data.repositories

import com.example.mindwell.app.data.datasources.remote.CheckinRemoteDataSource
import com.example.mindwell.app.data.mappers.CheckinMapper
import com.example.mindwell.app.domain.entities.CheckinPage
import com.example.mindwell.app.domain.repositories.CheckinRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do repositório de check-ins.
 */
@Singleton
class CheckinRepositoryImpl @Inject constructor(
    private val remoteDataSource: CheckinRemoteDataSource
) : CheckinRepository {
    /**
     * Obtém os check-ins do usuário com paginação e filtro.
     * @param page Número da página
     * @param size Tamanho da página
     * @param from Data inicial para filtro
     * @param to Data final para filtro
     * @return Página de check-ins
     */
    override suspend fun getCheckins(
        page: Int,
        size: Int,
        from: LocalDate?,
        to: LocalDate?
    ): CheckinPage {
        val checkinPageDto = remoteDataSource.getCheckins(page, size, from, to)
        return CheckinMapper.mapToDomain(checkinPageDto)
    }
} 