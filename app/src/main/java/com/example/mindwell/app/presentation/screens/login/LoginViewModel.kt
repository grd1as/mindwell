package com.example.mindwell.app.presentation.screens.login

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindwell.app.R
import com.example.mindwell.app.data.network.AuthApi
import com.example.mindwell.app.data.network.LoginRequest
import com.example.mindwell.app.data.network.TokenStore
//import com.google.android.gms.auth.api.identity.SignInClient
//import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val app: Application,
    private val api: AuthApi
) : AndroidViewModel(app) {

    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(app.getString(R.string.web_client_id))   // client-id WEB
        .build()

    private val gsc: GoogleSignInClient = GoogleSignIn.getClient(app, gso)

    fun signInIntent() = gsc.signInIntent

    fun handleResult(data: Intent?, onSuccess: () -> Unit, onError: (String) -> Unit) {
        try {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data)
                .getResult(ApiException::class.java)
            val idToken = account.idToken ?: throw IllegalStateException("Sem idToken")

            viewModelScope.launch {
                try {
                    val jwt = api.login(LoginRequest(idToken)).jwt
                    TokenStore.save(jwt, app)
                    onSuccess()
                } catch (e: Exception) {
                    onError(e.message ?: "Erro login")
                }
            }
        } catch (e: Exception) {
            onError(e.localizedMessage ?: "Falha")
        }
    }

    fun logout(onDone: () -> Unit) = viewModelScope.launch {
        TokenStore.clear(app)
        gsc.signOut().addOnCompleteListener { onDone() }
    }
}
