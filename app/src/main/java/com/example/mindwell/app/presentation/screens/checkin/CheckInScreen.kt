package com.example.mindwell.app.presentation.screens.checkin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mindwell.app.R

data class MoodOption(
    val value: Int,
    val icon: ImageVector,
    val label: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInScreen(
    navController: NavController,
    viewModel: CheckInViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Observe o estado salvo para navegar de volta
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            navController.popBackStack()
        }
    }
    
    // Mostrar mensagens de erro
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(message = error)
        }
    }
    
    // Opções de humor
    val moodOptions = listOf(
        MoodOption(
            value = 1,
            icon = Icons.Default.FavoriteBorder,
            label = stringResource(id = R.string.mood_very_bad),
            color = Color(0xFFE53935)
        ),
        MoodOption(
            value = 2,
            icon = Icons.Default.FavoriteBorder,
            label = stringResource(id = R.string.mood_bad),
            color = Color(0xFFEF6C00)
        ),
        MoodOption(
            value = 3,
            icon = Icons.Default.Face,
            label = stringResource(id = R.string.mood_neutral),
            color = Color(0xFFFDD835)
        ),
        MoodOption(
            value = 4,
            icon = Icons.Default.Favorite,
            label = stringResource(id = R.string.mood_good),
            color = Color(0xFF43A047)
        ),
        MoodOption(
            value = 5,
            icon = Icons.Default.Favorite,
            label = stringResource(id = R.string.mood_very_good),
            color = Color(0xFF1E88E5)
        ),
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.check_in)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Seção de humor
            Column {
                Text(
                    text = stringResource(id = R.string.mood_question),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    moodOptions.forEach { option ->
                        MoodSelector(
                            option = option,
                            isSelected = uiState.mood == option.value,
                            onClick = { viewModel.updateMood(option.value) }
                        )
                    }
                }
            }
            
            // Seção de nível de estresse
            Column {
                Text(
                    text = stringResource(id = R.string.stress_level),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color(0xFF43A047)
                    )
                    
                    Slider(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        value = uiState.stressLevel.toFloat(),
                        onValueChange = { viewModel.updateStressLevel(it.toInt()) },
                        valueRange = 1f..5f,
                        steps = 3
                    )
                    
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = Color(0xFFE53935)
                    )
                }
                
                Text(
                    text = "Nível ${uiState.stressLevel} de 5",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            
            // Seção de anotações
            OutlinedTextField(
                value = uiState.notes,
                onValueChange = { viewModel.updateNotes(it) },
                label = { Text(text = stringResource(id = R.string.notes)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            
            // Botão de salvar
            Button(
                onClick = { viewModel.saveCheckIn() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(text = stringResource(id = R.string.save))
            }
        }
    }
}

@Composable
fun MoodSelector(
    option: MoodOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (isSelected) option.color else Color.Transparent)
                .border(
                    width = 2.dp,
                    color = option.color,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = option.label,
                tint = if (isSelected) Color.White else option.color,
                modifier = Modifier.size(32.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = option.label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
} 