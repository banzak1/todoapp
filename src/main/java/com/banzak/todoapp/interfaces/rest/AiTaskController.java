package com.banzak.todoapp.interfaces.rest;

import com.banzak.todoapp.application.ai.AiTaskSuggester;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tasks/ai")
@Tag(name = "Tasks AI", description = "AI-powered Task Assistant API")
public class AiTaskController {

    private final AiTaskSuggester aiTaskSuggester;

    public AiTaskController(AiTaskSuggester aiTaskSuggester) {
        this.aiTaskSuggester = aiTaskSuggester;
    }

    @PostMapping("/suggest")
    @Operation(summary = "Suggest subtasks, priority and refined description using AI")
    public ResponseEntity<AiTaskSuggester.AiSuggestion> suggest(@Valid @RequestBody AiSuggestRequest request) {
        var suggestion = aiTaskSuggester.suggestSubtasks(request.title(), request.description());
        return ResponseEntity.ok(suggestion);
    }

    public record AiSuggestRequest(
            @NotBlank(message = "Title is required")
            String title,
            String description
    ) {}
}
