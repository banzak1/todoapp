package com.banzak.todoapp.infrastructure.ai;

import com.banzak.todoapp.infrastructure.observability.MicrometerAiSuggestionMetrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LangChain4jTaskSuggesterObservabilityTest {

    private final LangChain4jTaskSuggesterService service = mock(LangChain4jTaskSuggesterService.class);
    private final SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
    private final LangChain4jTaskSuggester suggester = new LangChain4jTaskSuggester(
            service,
            new MicrometerAiSuggestionMetrics(meterRegistry));

    @Test
    void shouldRecordSuccessfulSuggestionWithoutUsingRequestContentAsTags() {
        when(service.suggest("Sensitive title", "Sensitive description"))
                .thenReturn(new LangChain4jSuggestion("HIGH", "Refined", List.of("First subtask")));

        suggester.suggestSubtasks("Sensitive title", "Sensitive description");

        var counter = meterRegistry.find("todoapp.ai.suggestions")
                .tag("outcome", "success")
                .counter();
        assertThat(counter.count()).isEqualTo(1.0);
        assertThat(counter.getId().getTags()).hasSize(1);
        assertThat(counter.getId().getTags().getFirst().getKey()).isEqualTo("outcome");
    }

    @Test
    void shouldRecordFailedSuggestionOnceWhenServiceThrows() {
        when(service.suggest("Title", "Description"))
                .thenThrow(new RuntimeException("LLM unavailable"));

        assertThatThrownBy(() -> suggester.suggestSubtasks("Title", "Description"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("LLM unavailable");

        var counter = meterRegistry.find("todoapp.ai.suggestions")
                .tag("outcome", "failure")
                .counter();
        assertThat(counter.count()).isEqualTo(1.0);
    }
}
