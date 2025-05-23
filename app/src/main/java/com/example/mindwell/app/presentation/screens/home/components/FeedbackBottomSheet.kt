package com.example.mindwell.app.presentation.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Bottom sheet para envio de feedback/report (Canal de Escuta)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackBottomSheet(
    categories: List<String>,
    selectedCategory: String,
    description: String,
    isSubmitting: Boolean,
    success: Boolean,
    errorMessage: String?,
    onCategorySelected: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit,
    bottomSheetState: SheetState
) {
    val categoryLabels = mapOf(
        "ASSÉDIO_MORAL" to "Assédio Moral",
        "ASSÉDIO_SEXUAL" to "Assédio Sexual",
        "DISCRIMINAÇÃO_RACIAL" to "Discriminação Racial",
        "DISCRIMINAÇÃO_DE_GÊNERO" to "Discriminação de Gênero",
        "VIOLÊNCIA_FÍSICA" to "Violência Física",
        "VIOLÊNCIA_VERBAL" to "Violência Verbal",
        "CONFLITO_INTERPESSOAL" to "Conflito Interpessoal",
        "SAÚDE_E_SEGURANÇA" to "Assuntos de Saúde e Segurança",
        "INFRAESTRUTURA_INADEQUADA" to "Infraestrutura Inadequada",
        "EQUIPAMENTO_QUEBRADO" to "Equipamento Quebrado",
        "ERGONOMIA_INADEQUADA" to "Ergonomia Inadequada",
        "OUTRO" to "Outro"
    )
    
    var expanded by remember { mutableStateOf(false) }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        modifier = Modifier.fillMaxHeight(0.9f),
        dragHandle = { 
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(2.dp)
                        )
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cabeçalho com ícone
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "Canal de Escuta",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Descrição do que é o Canal de Escuta
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "O que é o Canal de Escuta?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Este é um espaço seguro para você relatar situações desconfortáveis, denunciar comportamentos inadequados ou sugerir melhorias no ambiente de trabalho.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Todas as informações são tratadas com confidencialidade e anonimato. Seu bem-estar é nossa prioridade.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            if (success) {
                // Mensagem de sucesso
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Sucesso",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Feedback enviado com sucesso!",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Agradecemos sua contribuição para um ambiente melhor.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                // Seleção de categoria
                Text(
                    text = "Selecione uma categoria:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = categoryLabels[selectedCategory] ?: "Selecione uma categoria",
                        onValueChange = { },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        label = { Text("Categoria") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        shape = RoundedCornerShape(16.dp)
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(categoryLabels[category] ?: category) },
                                onClick = {
                                    onCategorySelected(category)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                
                // Campo de descrição
                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    label = { Text("Descreva a situação") },
                    placeholder = { Text("Descreva a situação de forma clara e detalhada...") },
                    shape = RoundedCornerShape(16.dp)
                )
                
                // Texto sobre anonimato
                Text(
                    text = "Sua identidade será protegida. Somente o departamento de RH terá acesso a esta informação.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Mensagem de erro, se houver
                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                // Botões de ação
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = onSubmit,
                        enabled = !isSubmitting,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Enviar")
                        }
                    }
                }
            }
            
            // Espaçamento no final
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
} 