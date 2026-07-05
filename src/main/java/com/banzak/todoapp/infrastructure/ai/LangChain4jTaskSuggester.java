package com.banzak.todoapp.infrastructure.ai;

import com.banzak.todoapp.application.ai.AiTaskSuggester;

public class LangChain4jTaskSuggester implements AiTaskSuggester {

    private final LangChain4jTaskSuggesterService service;

    public LangChain4jTaskSuggester(LangChain4jTaskSuggesterService service) {
        this.service = service;
    }

    @Override
    public AiSuggestion suggestSubtasks(String title, String description) {
        var response = service.suggest(title, description != null ? description : "");
        return new AiSuggestion(
                response.suggestedPriority(),
                response.refinedDescription(),
                response.suggestedSubtasks()
        );
    }
}
