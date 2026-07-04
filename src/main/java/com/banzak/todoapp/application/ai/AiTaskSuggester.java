package com.banzak.todoapp.application.ai;

import java.util.List;

public interface AiTaskSuggester {
    
    AiSuggestion suggestSubtasks(String title, String description);

    record AiSuggestion(
            String suggestedPriority,
            String refinedDescription,
            List<String> suggestedSubtasks
    ) {}
}
