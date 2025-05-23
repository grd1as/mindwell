package com.example.mindwell.app.presentation.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mindwell.app.R
import com.example.mindwell.app.common.navigation.AppDestinations
import com.example.mindwell.app.domain.entities.OnboardingPage

/**
 * Tela de onboarding.
 */
@Composable
fun OnboardingScreen(
    navController: NavController,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when {
            uiState.isLoading -> {
                LoadingIndicator()
            }
            uiState.error != null -> {
                ErrorMessage(uiState.error!!)
            }
            else -> {
                // Mostra o conteúdo normal de onboarding
                OnboardingContent(
                    uiState = uiState,
                    onNext = {
                        if (!viewModel.moveToNextPage()) {
                            // Se estiver na última página, conclui o onboarding e navega para tela de login
                            viewModel.completeOnboarding()
                            navController.navigate(AppDestinations.LOGIN) {
                                popUpTo(AppDestinations.ONBOARDING) { inclusive = true }
                            }
                        }
                    },
                    onPrevious = {
                        viewModel.moveToPreviousPage()
                    },
                    onPageSelected = { pageIndex ->
                        viewModel.navigateToPage(pageIndex)
                    },
                    onCheckboxChanged = { key, isChecked ->
                        viewModel.updateConfirmationCheckbox(key, isChecked)
                    }
                )
            }
        }
    }
}

/**
 * Conteúdo principal da tela de onboarding.
 */
@Composable
private fun OnboardingContent(
    uiState: OnboardingUiState,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onPageSelected: (Int) -> Unit,
    onCheckboxChanged: (String, Boolean) -> Unit
) {
    val scrollState = rememberScrollState()
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        )

        // Conteúdo da página atual (scrollable)
        if (uiState.pages.isNotEmpty()) {
            val currentPage = uiState.pages[uiState.currentPage]
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 120.dp) // Adicionando espaço para os botões fixos
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(700)) + slideInHorizontally(tween(700)),
                    exit = fadeOut(tween(700)) + slideOutHorizontally(tween(700))
                ) {
                    // Verificamos se está na página de confirmações (ID 999)
                    if (currentPage.id == 999) {
                        ConfirmationsPageContent(
                            uiState = uiState,
                            onCheckboxChanged = onCheckboxChanged
                        )
                    } else {
                        PageContent(page = currentPage)
                    }
                }
            }
        }
        
        // Footer: indicadores de página e botões de navegação (fixos na parte inferior)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .padding(bottom = 32.dp, start = 16.dp, end = 16.dp, top = 16.dp)
        ) {
            // Indicadores de página
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 28.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                uiState.pages.forEachIndexed { index, _ ->
                    PageIndicator(
                        isSelected = index == uiState.currentPage,
                        onClick = { onPageSelected(index) }
                    )
                    if (index < uiState.pages.size - 1) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
            
            // Botões de navegação
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botão de voltar (visível apenas se não estiver na primeira página)
                if (uiState.currentPage > 0) {
                    IconButton(
                        onClick = onPrevious,
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    // Espaçador para manter o layout quando o botão de voltar não está visível
                    Spacer(modifier = Modifier.size(48.dp))
                }
                
                // Verificar se estamos na última página
                val isLastPage = uiState.currentPage == uiState.pages.size - 1
                
                // Verificar se estamos na página de confirmações
                val isConfirmationPage = uiState.pages.getOrNull(uiState.currentPage)?.id == 999
                
                // Determinar se o botão de avançar deve estar habilitado
                val isNextButtonEnabled = !isConfirmationPage || uiState.allRequiredConfirmed
                
                // Botão de avançar/finalizar
                Button(
                    onClick = onNext,
                    shape = RoundedCornerShape(24.dp),
                    enabled = isNextButtonEnabled,
                    modifier = Modifier
                        .height(48.dp)
                        .width(if (isLastPage) 160.dp else 140.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (isLastPage) {
                            Text(
                                text = "Começar",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Começar"
                            )
                        } else {
                            Text(
                                text = "Continuar",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Avançar"
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Conteúdo específico para a página de confirmações.
 */
@Composable
private fun ConfirmationsPageContent(
    uiState: OnboardingUiState,
    onCheckboxChanged: (String, Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Cabeçalho
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Imagem da página
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Confirmações",
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 16.dp)
            )
            
            // Título da página
            Text(
                text = "Antes de começar",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            // Descrição da página
            Text(
                text = "Por favor, leia e confirme os termos abaixo para continuar:",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        
        // Card de confirmações
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Checkbox para Termos de Uso (obrigatório)
                CheckboxItem(
                    title = "Termos de Uso",
                    description = "Concordo com os Termos de Uso e Política de Privacidade do MindWell.",
                    isChecked = uiState.confirmationCheckboxes["termsAccepted"] ?: false,
                    isRequired = true,
                    onCheckedChange = { isChecked ->
                        onCheckboxChanged("termsAccepted", isChecked)
                    }
                )
                
                Divider(thickness = 0.5.dp)
                
                // Checkbox para Coleta de Dados (obrigatório)
                CheckboxItem(
                    title = "Coleta de Dados",
                    description = "Entendo que o MindWell coletará dados sobre meu uso para melhorar minha experiência.",
                    isChecked = uiState.confirmationCheckboxes["dataCollection"] ?: false,
                    isRequired = true,
                    onCheckedChange = { isChecked ->
                        onCheckboxChanged("dataCollection", isChecked)
                    }
                )
                
                Divider(thickness = 0.5.dp)
                
                // Checkbox para Notificações (opcional)
                CheckboxItem(
                    title = "Notificações",
                    description = "Desejo receber lembretes e dicas de bem-estar por notificações.",
                    isChecked = uiState.confirmationCheckboxes["notifications"] ?: false,
                    isRequired = false,
                    onCheckedChange = { isChecked ->
                        onCheckboxChanged("notifications", isChecked)
                    }
                )
            }
        }
        
        // Informação sobre quais itens são obrigatórios
        Text(
            text = "* Itens obrigatórios para continuar",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

/**
 * Item de checkbox estilizado.
 */
@Composable
private fun CheckboxItem(
    title: String,
    description: String,
    isChecked: Boolean,
    isRequired: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Confirmando que estamos usando o ícone correto
        if (isChecked) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Marcado",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(top = 2.dp, end = 16.dp)
                    .size(24.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .padding(top = 2.dp, end = 16.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant, CircleShape)
            )
        }
        
        // Conteúdo textual
        Column {
            // Título com indicador de obrigatório se necessário
            Text(
                text = "$title ${if (isRequired) "*" else ""}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Descrição
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Conteúdo de uma página do onboarding.
 */
@Composable
private fun PageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Cabeçalho com layout assimétrico
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            // Título da página - alinhado à esquerda com destaque visual
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .align(Alignment.CenterStart)
            ) {
                Text(
                    text = page.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // Linha decorativa
                Divider(
                    modifier = Modifier
                        .width(80.dp)
                        .padding(bottom = 16.dp),
                    thickness = 4.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                
                // Descrição da página
                Text(
                    text = page.description,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Start
                )
            }
        }
        
        // Imagem da página - tamanho aumentado com posicionamento assimétrico
        val imageRes = when (page.image_resource) {
            "welcome" -> R.drawable.ic_launcher_foreground // Substitua por imagens reais
            "checkin" -> R.drawable.ic_launcher_foreground
            "reports" -> R.drawable.ic_launcher_foreground
            "resources" -> R.drawable.ic_launcher_foreground
            else -> R.drawable.ic_launcher_foreground
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .align(Alignment.Center),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = page.title,
                        modifier = Modifier
                            .size(200.dp)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Indicador de página.
 */
@Composable
private fun PageIndicator(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val width by animateDpAsState(
        targetValue = if (isSelected) 24.dp else 8.dp,
        animationSpec = tween(durationMillis = 300),
        label = "indicatorWidth"
    )
    
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primary 
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .width(width)
            .height(8.dp)
            .padding(horizontal = 2.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    )
}

/**
 * Indicador de carregamento.
 */
@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

/**
 * Mensagem de erro.
 */
@Composable
private fun ErrorMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Erro: $message",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Red,
            textAlign = TextAlign.Center
        )
    }
} 