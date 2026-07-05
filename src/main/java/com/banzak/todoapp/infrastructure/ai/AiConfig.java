package com.banzak.todoapp.infrastructure.ai;

import com.banzak.todoapp.application.ai.AiTaskSuggester;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
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
    @Conditional(GroqApiKeySet.class)
    public ChatModel groqChatModel() {
        String apiKey = System.getenv("LLAMA_API_KEY");
        log.info("Criando ChatModel com Groq (Llama 3).");
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl("https://api.groq.com/openai/v1")
                .modelName("llama-3.3-70b-versatile")
                .temperature(0.7)
                .maxTokens(500)
                .build();
    }

    @Bean
    @Conditional(GroqApiKeySet.class)
    public LangChain4jTaskSuggesterService langchain4jTaskSuggesterService(ChatModel chatModel) {
        log.info("Inicializando serviço de IA declarativo da LangChain4j com Groq.");
        return AiServices.builder(LangChain4jTaskSuggesterService.class)
                .chatModel(chatModel)
                .build();
    }

    @Bean
    @Conditional(GroqApiKeySet.class)
    public AiTaskSuggester langchain4jTaskSuggester(LangChain4jTaskSuggesterService service) {
        log.info("Ativando adaptador de IA real (Groq/Llama 3).");
        return new LangChain4jTaskSuggester(service);
    }

    @Bean
    @ConditionalOnMissingBean(AiTaskSuggester.class)
    public AiTaskSuggester fallbackTaskSuggester() {
        log.info("Ativando adaptador de IA Mock (Sem chave de API configurada).");
        return new MockTaskSuggester();
    }

    /**
     * Condition that matches when LLAMA_API_KEY environment variable is set and non-empty.
     */
    public static class GroqApiKeySet implements org.springframework.context.annotation.Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            String apiKey = context.getEnvironment().getProperty("LLAMA_API_KEY");
            if (apiKey == null || apiKey.isBlank()) {
                log.info("LLAMA_API_KEY não definida. Usando MockTaskSuggester.");
                return false;
            }
            log.info("LLAMA_API_KEY detectada, ativando modo real (Groq/Llama 3).");
            return true;
        }
    }
}
