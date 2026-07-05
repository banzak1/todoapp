package com.banzak.todoapp.infrastructure.ai;

import com.banzak.todoapp.application.ai.AiTaskSuggester;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

@Configuration
public class AiConfig {

    private static final Logger log = LoggerFactory.getLogger(AiConfig.class);

    @Bean
    @Conditional(GeminiApiKeySet.class)
    public ChatModel geminiChatModel() {
        String apiKey = System.getenv("GEMINI_API_KEY");
        log.info("Criando ChatModel com Google Gemini.");
        return GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-2.0-flash")
                .temperature(0.7)
                .maxOutputTokens(500)
                .build();
    }

    @Bean
    @Conditional(GeminiApiKeySet.class)
    public LangChain4jTaskSuggesterService langchain4jTaskSuggesterService(ChatModel chatModel) {
        log.info("Inicializando serviço de IA declarativo da LangChain4j.");
        return AiServices.builder(LangChain4jTaskSuggesterService.class)
                .chatLanguageModel(chatModel)
                .build();
    }

    @Bean
    @Conditional(GeminiApiKeySet.class)
    public AiTaskSuggester langchain4jTaskSuggester(LangChain4jTaskSuggesterService service) {
        log.info("Ativando adaptador de IA real (Gemini).");
        return new LangChain4jTaskSuggester(service);
    }

    @Bean
    @ConditionalOnMissingBean(AiTaskSuggester.class)
    public AiTaskSuggester fallbackTaskSuggester() {
        log.info("Ativando adaptador de IA Mock (Sem chave de API configurada).");
        return new MockTaskSuggester();
    }

    /**
     * Condition that matches when GEMINI_API_KEY environment variable is set and non-empty.
     */
    public static class GeminiApiKeySet implements org.springframework.context.annotation.Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            String apiKey = context.getEnvironment().getProperty("GEMINI_API_KEY");
            if (apiKey == null || apiKey.isBlank()) {
                log.info("GEMINI_API_KEY não definida. Usando MockTaskSuggester.");
                return false;
            }
            return true;
        }
    }
}
