package com.example.mindwell.app.presentation.screens.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.example.mindwell.app.R
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
                nav.navigate(AppDestinations.ONBOARDING) { 
                    popUpTo(AppDestinations.LOGIN) { inclusive = true } 
                }
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
            // Logo ou Ícone do App
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Logo MindWell",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 16.dp)
            )
            
            // Título do App
            Text(
                text = "MindWell",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Subtítulo ou slogan
            Text(
                text = "Seu bem-estar em primeiro lugar",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 32.dp, vertical = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                modifier = Modifier
                    .padding(32.dp)
                    .height(50.dp)
                    .fillMaxWidth(0.8f),
                onClick = { launcher.launch(vm.signInIntent()) }
            ) {
                Text("Entrar com Google")
            }
        }
    }
}
