package com.example.mindwell.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.mindwell.app.common.design_system.theme.MindWellTheme
import com.example.mindwell.app.common.navigation.AppNavigation
import com.example.mindwell.app.data.services.ReminderDialogManager
import com.example.mindwell.app.data.services.ReminderService
import com.example.mindwell.app.presentation.MainViewModel
import com.example.mindwell.app.presentation.NavigationState
import com.example.mindwell.app.presentation.components.ReminderDialog
import com.example.mindwell.app.presentation.screens.LoadingScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Atividade principal do aplicativo MindWell.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var reminder_dialog_manager: ReminderDialogManager
    
    @Inject
    lateinit var reminder_service: ReminderService
    
    private lateinit var reminder_broadcast_receiver: BroadcastReceiver
    
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_MindWell)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Não iniciar sistema de lembretes automaticamente
        // reminder_service.start_reminder_system() // Movido para após login
        
        // Configurar BroadcastReceiver para dialogs de lembrete
        setupReminderBroadcastReceiver()
        
        setContent {
            MindWellTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MindWellApp(reminder_dialog_manager)
                }
            }
        }
    }
    
    private fun setupReminderBroadcastReceiver() {
        reminder_broadcast_receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == "com.example.mindwell.SHOW_REMINDER_DIALOG") {
                    reminder_dialog_manager.show_reminder()
                }
            }
        }
        
        val filter = IntentFilter("com.example.mindwell.SHOW_REMINDER_DIALOG")
        
        // Para Android 13+ (API 33+), especificar se o receiver é exportado
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                reminder_broadcast_receiver, 
                filter, 
                android.content.Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            registerReceiver(reminder_broadcast_receiver, filter)
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        if (::reminder_broadcast_receiver.isInitialized) {
            unregisterReceiver(reminder_broadcast_receiver)
        }
    }
    
    /**
     * Método público para iniciar lembretes após login bem-sucedido
     */
    fun start_reminders_after_login() {
        reminder_service.start_reminder_system()
    }
}

/**
 * Composable principal do aplicativo que gerencia a navegação inicial.
 */
@Composable
fun MindWellApp(
    reminder_dialog_manager: ReminderDialogManager,
    main_view_model: MainViewModel = hiltViewModel()
) {
    val nav_controller = rememberNavController()
    val should_show_reminder by reminder_dialog_manager.should_show_reminder.collectAsState()
    
    when (val state = main_view_model.navigation_state) {
        is NavigationState.Loading -> {
            LoadingScreen(message = "Iniciando MindWell...")
        }
        
        is NavigationState.Ready -> {
            AppNavigation(
                navController = nav_controller,
                initialScreen = state.destination
            )
        }
        
        is NavigationState.Error -> {
            // Em caso de erro, mostra a mensagem por um momento e depois carrega o onboarding
            LoadingScreen(message = state.message)
            // Você pode adicionar aqui um delay e depois navegar para o onboarding
            // ou mostrar uma tela de erro específica
        }
    }
    
    // Dialog global de lembrete que aparece sobre qualquer tela
    if (should_show_reminder) {
        ReminderDialog(
            onDismiss = {
                reminder_dialog_manager.hide_reminder()
            },
            onCheckinNow = {
                reminder_dialog_manager.hide_reminder()
                // Navegar para a tela de check-in
                nav_controller.navigate("checkin") {
                    // Limpar pilha se necessário
                }
            },
            onRemindLater = {
                reminder_dialog_manager.snooze_reminder()
            }
        )
    }
}
