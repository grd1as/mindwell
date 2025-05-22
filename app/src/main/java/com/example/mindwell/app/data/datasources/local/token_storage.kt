package com.example.mindwell.app.data.datasources.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

private val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "token_preferences"
)

/**
 * Classe para gerenciar o armazenamento do token JWT.
 */
@Singleton
class TokenStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val jwtTokenKey = stringPreferencesKey("jwt_token")
    private val expirationKey = longPreferencesKey("token_expiration")
    
    /**
     * Salva o token JWT e sua expiração.
     * @param token Token JWT
     * @param expiresIn Tempo de expiração em segundos
     */
    suspend fun saveJwtToken(token: String, expiresIn: Long) {
        val expirationTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(expiresIn)
        context.tokenDataStore.edit { preferences ->
            preferences[jwtTokenKey] = token
            preferences[expirationKey] = expirationTime
        }
    }
    
    /**
     * Obtém o token JWT armazenado.
     * @return Token JWT ou null se não existir ou estiver expirado
     */
    suspend fun getJwtToken(): String? {
        val tokenData = context.tokenDataStore.data.map { preferences ->
            val token = preferences[jwtTokenKey]
            val expiration = preferences[expirationKey] ?: 0L
            
            if (token != null && System.currentTimeMillis() < expiration) {
                token
            } else {
                null
            }
        }
        
        return tokenData.first()
    }
    
    /**
     * Verifica se o token está armazenado e válido.
     * @return true se o token existir e for válido
     */
    suspend fun hasValidToken(): Boolean {
        return getJwtToken() != null
    }
    
    /**
     * Remove o token armazenado.
     */
    suspend fun clearToken() {
        context.tokenDataStore.edit { preferences ->
            preferences.remove(jwtTokenKey)
            preferences.remove(expirationKey)
        }
    }
} 