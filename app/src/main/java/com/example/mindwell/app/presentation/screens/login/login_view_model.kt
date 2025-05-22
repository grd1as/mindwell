package com.example.mindwell.app.presentation.screens.login

import android.content.Context
import android.content.Intent
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
        if (data == null) {
            onError("Falha na autenticação: Nenhum dado recebido")
            return
        }
        
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(Exception::class.java)
            
            if (account == null) {
                onError("Falha na autenticação: Conta não encontrada")
                return
            }
            
            val idToken = account.idToken
            if (idToken == null) {
                onError("Falha na autenticação: Token não encontrado")
                return
            }
            
            viewModelScope.launch {
                loginUseCase(idToken)
                    .catch { e ->
                        onError("Falha na autenticação: ${e.message}")
                    }
                    .collect { result ->
                        if (result.isSuccess) {
                            onSuccess()
                        } else {
                            onError("Falha na autenticação: ${result.exceptionOrNull()?.message}")
                        }
                    }
            }
            
        } catch (e: Exception) {
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