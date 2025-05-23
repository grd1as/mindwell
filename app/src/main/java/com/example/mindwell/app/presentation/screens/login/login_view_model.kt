package com.example.mindwell.app.presentation.screens.login

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindwell.app.domain.usecases.auth.LoginUseCase
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ViewModel para tela de login.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val googleSignInClient: GoogleSignInClient
) : ViewModel() {
    
    // Estado de loading
    var isLoading by mutableStateOf(false)
        private set
    
    /**
     * Obtém intent para iniciar o fluxo de login do Google.
     * @return Intent para autenticação com Google
     */
    fun signInIntent(): Intent {
        return googleSignInClient.signInIntent
    }
    
    /**
     * Processa o resultado da tentativa de login.
     * @param data Intent com dados de resultado
     * @param onSuccess Callback para sucesso
     * @param onError Callback para erro
     */
    fun handleResult(
        data: Intent?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (isLoading) return // Previne múltiplas chamadas
        
        if (data == null) {
            onError("Falha na autenticação: Nenhum dado recebido")
            return
        }
        
        try {
            isLoading = true
            
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(Exception::class.java)
            
            if (account == null) {
                isLoading = false
                onError("Falha na autenticação: Conta não encontrada")
                return
            }
            
            val idToken = account.idToken
            if (idToken == null) {
                isLoading = false
                onError("Falha na autenticação: Token não encontrado")
                return
            }
            
            viewModelScope.launch {
                loginUseCase(idToken)
                    .catch { e ->
                        isLoading = false
                        onError("Falha na autenticação: ${e.message}")
                    }
                    .collect { result ->
                        isLoading = false
                        if (result.isSuccess) {
                            onSuccess()
                        } else {
                            onError("Falha na autenticação: ${result.exceptionOrNull()?.message}")
                        }
                    }
            }
            
        } catch (e: Exception) {
            isLoading = false
            onError("Falha na autenticação: ${e.message}")
        }
    }
}

/**
 * Factory para criar GoogleSignInClient.
 */
@Singleton
class GoogleSignInClientFactory @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Cria GoogleSignInClient.
     * @param serverClientId ID do cliente OAuth
     * @return GoogleSignInClient configurado
     */
    fun create(serverClientId: String): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(serverClientId)
            .requestEmail()
            .build()
        
        return GoogleSignIn.getClient(context, gso)
    }
} 