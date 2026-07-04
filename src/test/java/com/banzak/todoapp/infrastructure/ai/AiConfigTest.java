package com.banzak.todoapp.infrastructure.ai;

import com.banzak.todoapp.application.ai.AiTaskSuggester;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AiConfig")
class AiConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(AiConfig.class);

    @Test
    @DisplayName("should register MockTaskSuggester when OpenAI API key is missing")
    void shouldRegisterMock_whenApiKeyMissing() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(AiTaskSuggester.class);
            assertThat(context.getBean(AiTaskSuggester.class)).isInstanceOf(MockTaskSuggester.class);
        });
    }
}
