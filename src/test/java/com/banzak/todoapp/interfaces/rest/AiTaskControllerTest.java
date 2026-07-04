package com.banzak.todoapp.interfaces.rest;

import com.banzak.todoapp.application.ai.AiTaskSuggester;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebMvcTest(AiTaskController.class)
@DisplayName("AiTaskController")
class AiTaskControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AiTaskSuggester aiTaskSuggester;

    @Test
    @DisplayName("should return AI suggestion with 200 OK")
    void shouldReturnSuggestion_whenValidRequest() throws Exception {
        var request = new AiTaskController.AiSuggestRequest("Aprender Kubernetes", "Estudar para a entrevista");
        var mockSuggestion = new AiTaskSuggester.AiSuggestion(
                "HIGH",
                "Descrição refinada",
                List.of("Subtask 1", "Subtask 2")
        );

        when(aiTaskSuggester.suggestSubtasks(eq("Aprender Kubernetes"), eq("Estudar para a entrevista")))
                .thenReturn(mockSuggestion);

        var responseAssert = mockMvc.post().uri("/api/v1/tasks/ai/suggest")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .assertThat()
                .hasStatusOk();

        responseAssert.bodyJson().extractingPath("$.suggestedPriority").asString().isEqualTo("HIGH");
        responseAssert.bodyJson().extractingPath("$.refinedDescription").asString().isEqualTo("Descrição refinada");
        responseAssert.bodyJson().extractingPath("$.suggestedSubtasks[0]").asString().isEqualTo("Subtask 1");
    }

    @Test
    @DisplayName("should return 400 when title is blank")
    void shouldReturn400_whenBlankTitle() throws Exception {
        var request = new AiTaskController.AiSuggestRequest("", "Descrição");

        mockMvc.post().uri("/api/v1/tasks/ai/suggest")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .assertThat()
                .hasStatus(400);
    }
}
