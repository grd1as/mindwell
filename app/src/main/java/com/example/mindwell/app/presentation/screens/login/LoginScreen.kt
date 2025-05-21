package com.example.mindwell.app.presentation.screens.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.example.mindwell.app.common.navigation.AppDestinations

@Composable
fun LoginScreen(
    nav: NavController,
    vm: LoginViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope    = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        vm.handleResult(
            res.data,
            onSuccess = {
                nav.navigate(AppDestinations.HOME) { popUpTo(0) }
            },
            onError = { msg ->
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(msg)
                }
            }
        )
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                modifier = Modifier
                    .padding(32.dp)
                    .height(50.dp),
                onClick = { launcher.launch(vm.signInIntent()) }
            ) {
                Text("Entrar com Google")
            }

            Button(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .height(50.dp),
                onClick = {
                    nav.navigate(AppDestinations.HOME) { popUpTo(0) }
                }
            ) {
                Text("Pular")
            }
        }
    }
}