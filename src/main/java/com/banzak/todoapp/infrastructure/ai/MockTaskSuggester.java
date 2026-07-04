package com.banzak.todoapp.infrastructure.ai;

import com.banzak.todoapp.application.ai.AiTaskSuggester;
import java.util.List;

public class MockTaskSuggester implements AiTaskSuggester {

    @Override
    public AiSuggestion suggestSubtasks(String title, String description) {
        return new AiSuggestion(
                "HIGH",
                "[MOCK AI] Descrição refinada para a tarefa: \"" + title + "\". " + 
                (description != null && !description.isBlank() ? "Contexto original: " + description : ""),
                List.of(
                        "Analisar detalhadamente o escopo de: " + title,
                        "Preparar os pré-requisitos necessários para a execução",
                        "Desenvolver e validar os resultados esperados"
                )
        );
    }
}
