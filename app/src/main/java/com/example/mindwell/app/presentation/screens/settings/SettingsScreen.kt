package com.example.mindwell.app.presentation.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mindwell.app.R
import com.example.mindwell.app.common.navigation.AppDestinations
import com.example.mindwell.app.presentation.screens.login.LoginViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    vm: LoginViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    var loggingOut by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.Start
        ) {

            Text(text = "Conta", style = MaterialTheme.typography.titleMedium)

            Button(
                onClick = {
                    loggingOut = true
                    scope.launch {
                        vm.logout {
                            navController.navigate(AppDestinations.LOGIN) { popUpTo(0) }
                        }
                    }
                },
                enabled = !loggingOut
            ) {
                Text(stringResource(R.string.logout))
            }
        }
    }
}
