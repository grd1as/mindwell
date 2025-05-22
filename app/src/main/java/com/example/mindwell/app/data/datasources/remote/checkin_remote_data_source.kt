package com.example.mindwell.app.data.datasources.remote

import com.example.mindwell.app.data.model.CheckinPageDTO
import com.example.mindwell.app.data.network.ApiService
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fonte de dados remota para check-ins.
 */
@Singleton
class CheckinRemoteDataSource @Inject constructor(
    private val apiService: ApiService
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    
    /**
     * Obtém os check-ins do usuário com paginação e filtro.
     * @param page Número da página
     * @param size Tamanho da página
     * @param from Data inicial para filtro
     * @param to Data final para filtro
     * @return Página de check-ins
     */
    suspend fun getCheckins(
        page: Int? = null,
        size: Int? = null,
        from: LocalDate? = null,
        to: LocalDate? = null
    ): CheckinPageDTO {
        val fromString = from?.format(dateFormatter)
        val toString = to?.format(dateFormatter)
        
        return apiService.getCheckins(page, size, fromString, toString)
    }
} 