package com.banzak.todoapp.infrastructure.observability;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MicrometerTaskOperationMetricsTest {

    private final SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
    private final MicrometerTaskOperationMetrics taskOperationMetrics =
            new MicrometerTaskOperationMetrics(meterRegistry);

    @Test
    void shouldIncrementCreatedCounter_withApiSource() {
        taskOperationMetrics.recordCreated();

        var counter = meterRegistry.find("todoapp.tasks.created")
                .tag("source", "api")
                .counter();

        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    void shouldIncrementUpdatedCounter_withApiSource() {
        taskOperationMetrics.recordUpdated();

        var counter = meterRegistry.find("todoapp.tasks.updated")
                .tag("source", "api")
                .counter();

        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }
}
