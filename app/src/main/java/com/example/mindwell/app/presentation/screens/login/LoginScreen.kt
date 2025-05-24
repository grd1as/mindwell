package com.example.mindwell.app.presentation.screens.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.mindwell.app.R
import com.example.mindwell.app.common.navigation.AppDestinations
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    nav: NavController,
    vm: LoginViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showLogoAnimation by remember { mutableStateOf(false) }
    var showContentAnimation by remember { mutableStateOf(false) }
    var showButtonsAnimation by remember { mutableStateOf(false) }
    
    // Logo gradient colors
    val logoColorStart = Color(0xFFF29F9F)
    val logoColorEnd = Color(0xFFFF80B3)
    
    // Animações sequenciadas
    LaunchedEffect(Unit) {
        delay(100)
        showLogoAnimation = true
        delay(400)
        showContentAnimation = true
        delay(200)
        showButtonsAnimation = true
    }

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

    // Cores para usar nos elementos de design
    val primaryLight = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    val primaryMedium = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
    val secondaryLight = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
    val tertiaryLight = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Background shapes
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Círculo grande no canto superior esquerdo
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(primaryLight, Color.Transparent),
                        center = Offset(size.width * 0.1f, size.height * 0.1f),
                        radius = size.width * 0.6f
                    ),
                    center = Offset(size.width * 0.1f, size.height * 0.1f),
                    radius = size.width * 0.6f
                )
                
                // Círculo no canto inferior direito
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(secondaryLight, Color.Transparent),
                        center = Offset(size.width * 0.9f, size.height * 0.85f),
                        radius = size.width * 0.4f
                    ),
                    center = Offset(size.width * 0.9f, size.height * 0.85f),
                    radius = size.width * 0.4f
                )
                
                // Forma abstrata no meio
                val path = Path().apply {
                    val centerX = size.width * 0.6f
                    val centerY = size.height * 0.45f
                    val radius = size.width * 0.3f
                    
                    moveTo(
                        centerX + radius * cos(0f),
                        centerY + radius * sin(0f)
                    )
                    
                    for (i in 1..7) {
                        val angle = i * (2 * Math.PI / 7)
                        val r = radius * (0.8f + 0.2f * i % 3)
                        lineTo(
                            centerX + r * cos(angle).toFloat(),
                            centerY + r * sin(angle).toFloat()
                        )
                    }
                    
                    close()
                }
                
                drawPath(
                    path = path,
                    brush = Brush.radialGradient(
                        colors = listOf(tertiaryLight, Color.Transparent),
                        center = Offset(size.width * 0.6f, size.height * 0.45f),
                        radius = size.width * 0.3f
                    )
                )
            }
            
            // Conteúdo principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Área superior - Logo e título
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(top = 60.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Logo animado
                    AnimatedVisibility(
                        visible = showLogoAnimation,
                        enter = fadeIn(tween(1000)) + 
                                slideInVertically(tween(1000), initialOffsetY = { -it })
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data("file:///android_asset/mindwell-logo.svg")
                                    .decoderFactory(SvgDecoder.Factory())
                                    .build(),
                                contentDescription = "MindWell Logo",
                                modifier = Modifier.size(64.dp),
                                contentScale = ContentScale.Fit
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            // Logo outline como texto
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data("file:///android_asset/mindwell-typo.svg")
                                    .decoderFactory(SvgDecoder.Factory())
                                    .build(),
                                contentDescription = "MindWell Logo Text",
                                modifier = Modifier.height(32.dp),
                                contentScale = ContentScale.FillHeight
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(40.dp))
                    
                    // Textos descritivos
                    AnimatedVisibility(
                        visible = showContentAnimation,
                        enter = fadeIn(tween(1000)) + 
                                slideInVertically(tween(1000), initialOffsetY = { it / 2 })
                    ) {
                        Column {
                            Text(
                                text = "Bem-vindo ao seu espaço de saúde mental",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 36.sp,
                                    lineHeight = 44.sp
                                ),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = "Um lugar para cuidar do seu bem-estar e encontrar equilíbrio emocional no dia a dia",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                // Área inferior - Botões
                AnimatedVisibility(
                    visible = showButtonsAnimation,
                    enter = fadeIn(tween(1200)) + 
                            slideInVertically(tween(1200), initialOffsetY = { it / 2 })
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 40.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Botão de login com o Google
                        Button(
                            onClick = { 
                                if (!vm.isLoading) {
                                    launcher.launch(vm.signInIntent()) 
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            shape = RoundedCornerShape(30.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = logoColorStart,
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 6.dp,
                                pressedElevation = 8.dp
                            ),
                            enabled = !vm.isLoading
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                if (vm.isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    // Ícone do Google em um círculo
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(Color.White, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_google),
                                            contentDescription = "Google",
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.width(16.dp))
                                
                                Text(
                                    text = if (vm.isLoading) "Entrando..." else "Continuar com o Google",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Texto de termos e políticas
                        Text(
                            text = "Ao continuar, você concorda com nossos Termos de Uso e Política de Privacidade",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}