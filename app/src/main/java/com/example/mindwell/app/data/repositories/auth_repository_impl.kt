package com.example.mindwell.app.data.repositories

import android.content.Context
import android.content.SharedPreferences
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
    private val remoteDataSource: AuthRemoteDataSource
) : AuthRepository {
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    }
    
    /**
     * Realiza login com token do Google.
     * @param idToken Token de ID do Google
     * @return Usuário autenticado
     */
    override suspend fun login(idToken: String): User? {
        return try {
            val response = remoteDataSource.login(idToken)
            // Salva o token JWT nas preferências
            prefs.edit()
                .putString("jwt", response.jwt)
                .putLong("expiration", System.currentTimeMillis() + response.expiresIn * 1000)
                .apply()
                
            // Retorna um usuário com o token JWT
            User(
                jwt = response.jwt,
                expiresIn = response.expiresIn
            )
        } catch (e: Exception) {
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
        prefs.edit()
            .remove("jwt")
            .remove("expiration")
            .apply()
        
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
    override fun isAuthenticated(): Boolean {
        val jwt = prefs.getString("jwt", null) ?: return false
        val expiration = prefs.getLong("expiration", 0)
        return jwt.isNotEmpty() && expiration > System.currentTimeMillis()
    }
    
    /**
     * Obtém o token JWT.
     * @return Token JWT ou null se não estiver autenticado
     */
    override fun getJwtToken(): String? {
        return if (isAuthenticated()) {
            prefs.getString("jwt", null)
        } else null
    }
} 