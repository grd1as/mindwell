package com.example.mindwell.app.data.datasources.local

import android.content.Context
import android.util.Log
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
    private val TAG = "TokenStorage"
    private val jwtTokenKey = stringPreferencesKey("jwt_token")
    private val expirationKey = longPreferencesKey("token_expiration")
    
    /**
     * Salva o token JWT e sua expiração.
     * @param token Token JWT
     * @param expiresIn Tempo de expiração em segundos
     */
    suspend fun saveJwtToken(token: String, expiresIn: Long) {
        val expirationTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(expiresIn)
        Log.d(TAG, "💾 Salvando token JWT:")
        Log.d(TAG, "  Token: ${token.take(20)}...")
        Log.d(TAG, "  Expira em: $expiresIn segundos")
        Log.d(TAG, "  Tempo atual: ${System.currentTimeMillis()}")
        Log.d(TAG, "  Tempo de expiração: $expirationTime")
        
        context.tokenDataStore.edit { preferences ->
            preferences[jwtTokenKey] = token
            preferences[expirationKey] = expirationTime
        }
        
        Log.d(TAG, "✅ Token salvo com sucesso no DataStore")
    }
    
    /**
     * Obtém o token JWT armazenado.
     * @return Token JWT ou null se não existir ou estiver expirado
     */
    suspend fun getJwtToken(): String? {
        Log.d(TAG, "🔍 Buscando token JWT do DataStore...")
        
        val tokenData = context.tokenDataStore.data.map { preferences ->
            val token = preferences[jwtTokenKey]
            val expiration = preferences[expirationKey] ?: 0L
            val currentTime = System.currentTimeMillis()
            
            Log.d(TAG, "📖 Dados lidos do DataStore:")
            Log.d(TAG, "  Token presente: ${token != null}")
            Log.d(TAG, "  Token: ${token?.take(20) ?: "null"}...")
            Log.d(TAG, "  Tempo de expiração: $expiration")
            Log.d(TAG, "  Tempo atual: $currentTime")
            Log.d(TAG, "  Expirado: ${currentTime >= expiration}")
            
            if (token != null && currentTime < expiration) {
                Log.d(TAG, "✅ Token válido encontrado")
                token
            } else {
                if (token == null) {
                    Log.w(TAG, "⚠️ Nenhum token encontrado")
                } else {
                    Log.w(TAG, "⚠️ Token expirado")
                }
                null
            }
        }
        
        val result = tokenData.first()
        Log.d(TAG, "🔄 Resultado final: ${if (result != null) "Token válido" else "Token ausente/expirado"}")
        return result
    }
    
    /**
     * Verifica se o token está armazenado e válido.
     * @return true se o token existir e for válido
     */
    suspend fun hasValidToken(): Boolean {
        val hasToken = getJwtToken() != null
        Log.d(TAG, "🎯 hasValidToken: $hasToken")
        return hasToken
    }
    
    /**
     * Remove o token armazenado.
     */
    suspend fun clearToken() {
        Log.d(TAG, "🗑️ Removendo token do DataStore...")
        context.tokenDataStore.edit { preferences ->
            preferences.remove(jwtTokenKey)
            preferences.remove(expirationKey)
        }
        Log.d(TAG, "✅ Token removido com sucesso")
    }
} 