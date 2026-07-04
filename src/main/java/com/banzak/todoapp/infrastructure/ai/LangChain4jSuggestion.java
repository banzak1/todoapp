package com.banzak.todoapp.infrastructure.ai;

import java.util.List;

public record LangChain4jSuggestion(
    String suggestedPriority,
    String refinedDescription,
    List<String> suggestedSubtasks
) {}
