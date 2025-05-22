package com.example.mindwell.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.mindwell.app.common.design_system.theme.MindWellTheme
import com.example.mindwell.app.common.navigation.AppDestinations
import com.example.mindwell.app.common.navigation.AppNavigation
import com.example.mindwell.app.domain.usecases.onboarding.GetOnboardingStateUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Activity principal do aplicativo.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var getOnboardingStateUseCase: GetOnboardingStateUseCase
    
    private val viewModel: MainViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MindWellTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val startDestination by viewModel.startDestination.collectAsState(initial = AppDestinations.ONBOARDING)
                    AppNavigation(initialScreen = startDestination)
                }
            }
        }
    }
}
