# 🤖 Configuração do Gemini AI - MindWell

## 📋 Pré-requisitos

1. **Conta Google**: Necessária para acessar o Google AI Studio
2. **API Key do Gemini**: Gratuita com limites generosos

## 🔑 Obtendo a API Key

### Passo 1: Acesse o Google AI Studio
1. Vá para [Google AI Studio](https://aistudio.google.com/)
2. Faça login com sua conta Google
3. Aceite os termos de uso

### Passo 2: Gere sua API Key
1. No painel lateral, clique em **"Get API key"**
2. Clique em **"Create API key"**
3. Selecione um projeto existente ou crie um novo
4. Copie a API key gerada

### Passo 3: Configure no App
1. Abra o arquivo `app/src/main/res/values/strings.xml`
2. Substitua `YOUR_GEMINI_API_KEY_HERE` pela sua API key:

```xml
<string name="gemini_api_key">SUA_API_KEY_AQUI</string>
```

## 🚀 Funcionalidades Implementadas

### 📊 Análise Inteligente
- **Perfil do Usuário**: Analisa check-ins dos últimos 7 dias
- **Métricas Calculadas**: Estresse, ansiedade, energia
- **Padrões Identificados**: Humor, comportamento, preferências
- **Contexto Temporal**: Recomendações baseadas no horário e dia da semana

### 🎯 Conteúdo Personalizado
- **Recursos Adaptativos**: Exercícios baseados no estado emocional
- **Dicas Contextuais**: Sugestões práticas para o momento atual
- **Mensagens Motivadoras**: Texto personalizado e empático
- **Parsing JSON Real**: Processamento completo das respostas da API

### 🎨 Interface Integrada
- **Seção IA**: Destaque no topo da tela de Resources
- **Cards Visuais**: Design moderno para recursos e dicas
- **Refresh Inteligente**: Atualização sob demanda
- **Estados de Loading**: Feedback visual durante carregamento

## 📱 Como Usar

1. **Faça Check-ins**: O sistema precisa de dados para personalizar
2. **Acesse Resources**: Veja o conteúdo personalizado no topo
3. **Refresh**: Use o botão de atualizar para novas recomendações
4. **Monitor Logs**: Acompanhe a qualidade das respostas via LogCat

## 🔧 Estrutura Técnica

### Arquitetura
```
UI Layer (ResourcesScreen)
    ↓
Domain Layer (Use Cases)
    ↓
Data Layer (AiRepository)
    ↓
Service Layer (GeminiService)
    ↓
Gemini AI API
```

### Principais Classes
- `GeminiService`: Comunicação com API + parsing JSON
- `AiRepository`: Integração com dados do usuário
- `GetPersonalizedResourcesUseCase`: Lógica de negócio
- `ResourcesViewModel`: Estado da UI
- `GeminiDebugUtils`: Monitoramento de qualidade

### 🔍 Sistema de Debugging
- **Análise de Qualidade**: Monitora estrutura das respostas
- **Logs Detalhados**: Tracking completo do processo
- **Fallback Inteligente**: Dados mock quando API falha
- **Métricas de Token**: Estimativa de uso da API

## 🛡️ Segurança

### ⚠️ Importante
- **Nunca commite** a API key no Git
- **Use variáveis de ambiente** em produção
- **Monitore o uso** da API no Google Cloud Console

### 🔒 Boas Práticas
1. Adicione `strings.xml` ao `.gitignore` se necessário
2. Use BuildConfig para diferentes ambientes
3. Implemente rate limiting se necessário

## 📊 Limites da API (Gratuito)

- **Requests por minuto**: 15
- **Requests por dia**: 1,500
- **Tokens por minuto**: 32,000
- **Tokens por dia**: 50,000

## 🐛 Troubleshooting

### Erro: "API key inválida"
- Verifique se a API key foi copiada corretamente
- Confirme que a API do Gemini está habilitada no projeto

### Erro: "Quota exceeded"
- Aguarde o reset do limite (diário/por minuto)
- Considere implementar cache para reduzir chamadas

### Respostas com formato incorreto
- **Monitore logs**: Procure por "GeminiDebug" no LogCat
- **Status da resposta**: ✅ EXCELENTE, ⚠️ BOA, ❌ PROBLEMÁTICA, 💥 FALHA
- **Fallback automático**: Sistema usa dados mock em caso de erro

### Debug via LogCat
```
🤖 Gerando recursos personalizados
📝 Resposta do Gemini recebida
🔍 Analisando resposta do Gemini (resources)
📊 RELATÓRIO DE QUALIDADE - Status: ✅ EXCELENTE
✅ Recursos personalizados gerados com sucesso
```

## 🚀 Próximos Passos

1. ✅ **Parsing JSON Implementado**: Processamento completo das respostas
2. ✅ **Debug Tools**: Monitoramento de qualidade implementado
3. ✅ **Fallback Graceful**: Dados mock quando API falha
4. 🔄 **Cache Inteligente**: Reduzir chamadas desnecessárias
5. 🔄 **Analytics**: Monitorar eficácia das recomendações

## 📞 Suporte

Para dúvidas sobre a integração:
1. Consulte a [documentação oficial do Gemini](https://ai.google.dev/docs)
2. Verifique os logs do Android Studio (filtro: GeminiService, GeminiDebug)
3. Use `GeminiDebugUtils.createTestPrompt()` para testes

---

**🎉 Parabéns!** Você agora tem uma experiência de bem-estar mental powered by AI com parsing JSON completo! 🧠✨ 