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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
    onPageSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        
        // Conteúdo da página atual
        if (uiState.pages.isNotEmpty()) {
            val currentPage = uiState.pages[uiState.currentPage]
            
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInHorizontally(),
                exit = fadeOut() + slideOutHorizontally()
            ) {
                PageContent(page = currentPage)
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Indicadores de página
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
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
            
            // Botão de avançar
            Button(
                onClick = onNext,
                shape = CircleShape,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Avançar"
                )
            }
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
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Imagem da página
        val imageRes = when (page.imageResource) {
            "welcome" -> R.drawable.ic_launcher_foreground // Substitua por imagens reais
            "checkin" -> R.drawable.ic_launcher_foreground
            "reports" -> R.drawable.ic_launcher_foreground
            "resources" -> R.drawable.ic_launcher_foreground
            else -> R.drawable.ic_launcher_foreground
        }
        
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = page.title,
            modifier = Modifier
                .size(220.dp)
                .padding(bottom = 16.dp)
        )
        
        // Título da página
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        // Descrição da página
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
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