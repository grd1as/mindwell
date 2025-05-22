package com.example.mindwell.app.data.repositories

import com.example.mindwell.app.data.datasources.remote.SummaryRemoteDataSource
import com.example.mindwell.app.data.mappers.SummaryMapper
import com.example.mindwell.app.domain.entities.Summary
import com.example.mindwell.app.domain.repositories.SummaryRepository
import java.time.YearMonth
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do repositório de resumos/consolidações.
 */
@Singleton
class SummaryRepositoryImpl @Inject constructor(
    private val remoteDataSource: SummaryRemoteDataSource
) : SummaryRepository {
    /**
     * Obtém o resumo mensal dos check-ins.
     * @param month Mês para o qual obter o resumo
     * @return Resumo com estatísticas consolidadas
     */
    override suspend fun getCheckinMonthSummary(month: YearMonth): Summary {
        val summaryDto = remoteDataSource.getCheckinMonthSummary(month)
        return SummaryMapper.mapToDomain(summaryDto)
    }
    
    /**
     * Obtém o resumo semanal dos check-ins.
     * @param year Ano da semana
     * @param weekNumber Número da semana no ano
     * @return Resumo com estatísticas consolidadas
     */
    override suspend fun getCheckinWeekSummary(year: Int, weekNumber: Int): Summary {
        val summaryDto = remoteDataSource.getCheckinWeekSummary(year, weekNumber)
        return SummaryMapper.mapToDomain(summaryDto)
    }
} 