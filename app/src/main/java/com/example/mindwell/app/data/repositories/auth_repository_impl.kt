package com.example.mindwell.app.data.repositories

import android.content.Context
import android.content.SharedPreferences
import com.example.mindwell.app.data.datasources.local.TokenStorage
import com.example.mindwell.app.data.datasources.remote.AuthRemoteDataSource
import com.example.mindwell.app.domain.entities.User
import com.example.mindwell.app.domain.repositories.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do repositório de autenticação.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val remoteDataSource: AuthRemoteDataSource,
    private val tokenStorage: TokenStorage
) : AuthRepository {
    
    /**
     * Realiza login com token do Google.
     * @param idToken Token de ID do Google
     * @return Usuário autenticado
     */
    override suspend fun login(idToken: String): User? {
        return try {
            val response = remoteDataSource.login(idToken)
            
            android.util.Log.d("AuthRepository", "🔑 Login bem-sucedido, salvando JWT: ${response.jwt.take(20)}...")
            
            // Como o backend não retorna expiresIn, usamos um padrão de 24 horas (86400 segundos)
            // O JWT próprio contém a informação de expiração que será validada pelo backend
            val defaultExpirationHours = 24L
            val expiresIn = defaultExpirationHours * 3600 // 24 horas em segundos
            
            android.util.Log.d("AuthRepository", "⏰ Usando expiração padrão de $defaultExpirationHours horas ($expiresIn segundos)")
            
            // Salva o token JWT no TokenStorage
            tokenStorage.saveJwtToken(response.jwt, expiresIn)
            
            android.util.Log.d("AuthRepository", "✅ Token salvo no TokenStorage")
                
            // Retorna um usuário com o token JWT
            User(
                jwt = response.jwt,
                expiresIn = expiresIn
            )
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "❌ Erro no login: ${e.message}", e)
            null
        }
    }
    
    /**
     * Realiza logout completo do usuário.
     * 1. Faz logout no servidor
     * 2. Remove tokens JWT locais
     * 3. Revoga o acesso do Google
     * @return true se o logout foi bem-sucedido
     */
    override suspend fun logout(): Boolean {
        // 1. Tenta fazer logout no servidor
        val serverLogoutSuccess = try {
            remoteDataSource.logout()
        } catch (e: Exception) {
            // Se falhar no servidor, continuamos com o logout local
            false
        }
        
        // 2. Remove tokens locais independentemente do resultado do servidor
        tokenStorage.clearToken()
        
        // 3. Revoga o acesso do Google
        try {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
            val googleSignInClient = GoogleSignIn.getClient(context, gso)
            googleSignInClient.signOut()
            googleSignInClient.revokeAccess()
        } catch (e: Exception) {
            // Se falhar na revogação do Google, já fizemos o logout local,
            // então ainda consideramos o logout como parcialmente bem-sucedido
        }
        
        // Considera-se bem-sucedido se o token local foi removido
        return true
    }
    
    /**
     * Verifica se o usuário está autenticado.
     * @return true se o usuário estiver autenticado e o token for válido
     */
    override suspend fun isAuthenticated(): Boolean {
        return tokenStorage.hasValidToken()
    }
    
    /**
     * Obtém o token JWT.
     * @return Token JWT ou null se não estiver autenticado
     */
    override suspend fun getJwtToken(): String? {
        return tokenStorage.getJwtToken()
    }
} 