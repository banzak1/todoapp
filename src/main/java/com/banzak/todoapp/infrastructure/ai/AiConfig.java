package com.banzak.todoapp.infrastructure.ai;

import com.banzak.todoapp.application.ai.AiTaskSuggester;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    private static final Logger log = LoggerFactory.getLogger(AiConfig.class);

    @Bean
    @ConditionalOnProperty(name = "langchain4j.open-ai.chat-model.api-key")
    public LangChain4jTaskSuggesterService langchain4jTaskSuggesterService(ChatLanguageModel chatLanguageModel) {
        log.info("Inicializando serviço de IA declarativo da LangChain4j.");
        return AiServices.builder(LangChain4jTaskSuggesterService.class)
                .chatLanguageModel(chatLanguageModel)
                .build();
    }

    @Bean
    @ConditionalOnBean(LangChain4jTaskSuggesterService.class)
    public AiTaskSuggester langchain4jTaskSuggester(LangChain4jTaskSuggesterService service) {
        log.info("Ativando adaptador de IA real (LangChain4j).");
        return new LangChain4jTaskSuggester(service);
    }

    @Bean
    @ConditionalOnMissingBean(AiTaskSuggester.class)
    public AiTaskSuggester fallbackTaskSuggester() {
        log.info("Ativando adaptador de IA Mock (Sem chave de API configurada).");
        return new MockTaskSuggester();
    }
}
