package com.banzak.todoapp.infrastructure.ai;

import com.banzak.todoapp.application.ai.AiTaskSuggester;
import com.banzak.todoapp.infrastructure.observability.MicrometerAiSuggestionMetrics;

public class LangChain4jTaskSuggester implements AiTaskSuggester {

    private final LangChain4jTaskSuggesterService service;
    private final MicrometerAiSuggestionMetrics metrics;

    public LangChain4jTaskSuggester(
            LangChain4jTaskSuggesterService service,
            MicrometerAiSuggestionMetrics metrics) {
        this.service = service;
        this.metrics = metrics;
    }

    @Override
    public AiSuggestion suggestSubtasks(String title, String description) {
        try {
            var response = service.suggest(title, description != null ? description : "");
            metrics.recordSuccess();
            return new AiSuggestion(
                    response.suggestedPriority(),
                    response.refinedDescription(),
                    response.suggestedSubtasks()
            );
        } catch (RuntimeException exception) {
            metrics.recordFailure();
            throw exception;
        }
    }
}
