package com.example.mindwell.app.data.services

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.mindwell.app.MainActivity
import com.example.mindwell.app.R
import java.util.*

class NotificationService(private val context: Context) {
    
    companion object {
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "mindwell_reminders"
        const val REMINDER_REQUEST_CODE = 1002
        const val ACTION_REMINDER = "com.example.mindwell.REMINDER"
        
        // Para teste: 5 segundos (5000ms), para produção: alterar conforme necessário
        const val REMINDER_INTERVAL_MS = 5 * 1000L // 5 segundos para teste
        
        private const val TAG = "NotificationService"
    }
    
    private val alarm_manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val notification_manager = NotificationManagerCompat.from(context)
    
    init {
        create_notification_channel()
    }
    
    private fun create_notification_channel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Lembretes de Check-in",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificações para lembretes de check-in diário"
                enableVibration(true)
                setShowBadge(true)
                enableLights(true)
                lightColor = android.graphics.Color.BLUE
                setBypassDnd(false)
            }
            
            val notification_manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notification_manager.createNotificationChannel(channel)
            
            Log.d(TAG, "📱 Canal de notificação criado com IMPORTANCE_HIGH")
        }
    }
    
    /**
     * Verifica se temos permissão para mostrar notificações
     */
    fun has_notification_permission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            notification_manager.areNotificationsEnabled()
        }
    }
    
    fun start_daily_reminders() {
        if (!has_notification_permission()) {
            Log.w(TAG, "❌ Sem permissão para notificações. Não iniciando lembretes.")
            return
        }
        
        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            action = ACTION_REMINDER
        }
        
        val pending_intent = PendingIntent.getBroadcast(
            context,
            REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Cancelar alarmes anteriores
        alarm_manager.cancel(pending_intent)
        
        // Agendar próximo lembrete em 5 segundos
        val trigger_time = System.currentTimeMillis() + REMINDER_INTERVAL_MS
        
        try {
            alarm_manager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                trigger_time,
                REMINDER_INTERVAL_MS,
                pending_intent
            )
            
            Log.d(TAG, "🔔 Lembretes agendados! Próximo em ${REMINDER_INTERVAL_MS}ms")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro ao agendar lembretes: ${e.message}", e)
        }
    }
    
    fun stop_daily_reminders() {
        val intent = Intent(context, ReminderBroadcastReceiver::class.java)
        val pending_intent = PendingIntent.getBroadcast(
            context,
            REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarm_manager.cancel(pending_intent)
        notification_manager.cancel(NOTIFICATION_ID)
        
        Log.d(TAG, "🔕 Lembretes cancelados")
    }
    
    fun show_reminder_notification() {
        if (!has_notification_permission()) {
            Log.w(TAG, "❌ Sem permissão para mostrar notificação")
            return
        }
        
        Log.d(TAG, "🔔 Mostrando notificação de lembrete")
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "checkin") // Para navegar direto para check-in
        }
        
        val pending_intent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Usando ícone padrão do Android
            .setContentTitle("🌟 Hora do Check-in!")
            .setContentText("Como você está se sentindo hoje?")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("🌟 Como você está se sentindo hoje? Faça seu check-in diário e acompanhe sua evolução emocional. Toque para abrir o app! 💝"))
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Prioridade alta
            .setContentIntent(pending_intent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 1000, 500, 1000)) // Vibração mais longa
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setDefaults(NotificationCompat.DEFAULT_ALL) // Todos os defaults (som, vibração, luz)
            .setOngoing(false) // Pode ser dispensada
            .setShowWhen(true) // Mostra horário
            .setWhen(System.currentTimeMillis())
            .setColor(android.graphics.Color.BLUE) // Cor azul
            .build()
        
        try {
            notification_manager.notify(NOTIFICATION_ID, notification)
            Log.d(TAG, "✅ Notificação exibida com sucesso - ID: $NOTIFICATION_ID")
            Log.d(TAG, "📋 Título: '🌟 Hora do Check-in!' | Texto: 'Como você está se sentindo hoje?'")
            
            // Log adicional para debug
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val areEnabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                manager.areNotificationsEnabled()
            } else {
                true
            }
            Log.d(TAG, "🔍 Notificações habilitadas no sistema: $areEnabled")
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro ao exibir notificação: ${e.message}", e)
        }
    }
    
    /**
     * Função de teste para verificar se notificações funcionam
     */
    fun show_test_notification() {
        if (!has_notification_permission()) {
            Log.w(TAG, "❌ Sem permissão para notificação de teste")
            return
        }
        
        Log.d(TAG, "🧪 Exibindo notificação de TESTE")
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("🧪 TESTE - MindWell")
            .setContentText("Se você vê isso, as notificações funcionam!")
            .setPriority(NotificationCompat.PRIORITY_MAX) // Prioridade máxima
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 2000, 1000, 2000)) // Vibração bem longa
            .build()
        
        try {
            notification_manager.notify(999, notification) // ID diferente
            Log.d(TAG, "✅ Notificação de TESTE enviada com ID: 999")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro na notificação de teste: ${e.message}", e)
        }
    }
}

/**
 * BroadcastReceiver que recebe os alarmes e exibe as notificações
 */
class ReminderBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ReminderBroadcastReceiver", "🚨 Alarme recebido! Action: ${intent.action}")
        
        if (intent.action == NotificationService.ACTION_REMINDER) {
            val checkin_manager = CheckinManager(context)
            
            // Só mostra notificação se ainda não fez check-in hoje
            if (!checkin_manager.has_checked_in_today()) {
                Log.d("ReminderBroadcastReceiver", "✅ Pode mostrar notificação - ainda não fez check-in hoje")
                val notification_service = NotificationService(context)
                notification_service.show_reminder_notification()
            } else {
                Log.d("ReminderBroadcastReceiver", "⏭️ Pulando notificação - já fez check-in hoje")
            }
        }
    }
} 