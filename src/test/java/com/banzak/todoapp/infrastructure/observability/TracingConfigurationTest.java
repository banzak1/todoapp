package com.banzak.todoapp.infrastructure.observability;

import com.banzak.todoapp.TodoappApplication;
import com.banzak.todoapp.interfaces.rest.observability.CorrelationIdFilter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

class TracingConfigurationTest {

    @Test
    void shouldKeepTracingAndExportDisabledByDefaultWhileCorrelationIdRemainsAvailable() {
        try (ConfigurableApplicationContext context = application().run()) {
            assertThat(context.getEnvironment().getProperty("management.tracing.enabled", Boolean.class))
                    .isFalse();
            assertThat(context.getEnvironment().getProperty("management.otlp.tracing.export.enabled", Boolean.class))
                    .isFalse();
            assertThat(context.getBean(CorrelationIdFilter.class)).isNotNull();
        }
    }

    @Test
    void shouldConfigureLocalOtlpOnlyWhenObservabilityProfileIsExplicitlyEnabled() {
        try (ConfigurableApplicationContext context = application()
                .profiles("observability")
                .run(
                        "--management.tracing.enabled=true",
                        "--management.otlp.tracing.export.enabled=true")) {
            assertThat(context.getEnvironment().getProperty("management.tracing.enabled", Boolean.class))
                    .isTrue();
            assertThat(context.getEnvironment().getProperty("management.otlp.tracing.export.enabled", Boolean.class))
                    .isTrue();
            assertThat(context.getEnvironment().getProperty("management.otlp.tracing.endpoint"))
                    .isEqualTo("http://localhost:4318/v1/traces");
        }
    }

    private SpringApplicationBuilder application() {
        return new SpringApplicationBuilder(TodoappApplication.class)
                .properties(
                        "spring.main.web-application-type=none",
                        "spring.kafka.listener.auto-startup=false");
    }
}
