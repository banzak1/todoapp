package com.banzak.todoapp.infrastructure.messaging;

import com.banzak.todoapp.infrastructure.observability.kafka.KafkaConsumerCorrelationContext;
import com.banzak.todoapp.infrastructure.observability.kafka.KafkaCorrelationId;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KafkaTaskEventConsumerTest {

    private final SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
    private final KafkaConsumerCorrelationContext correlationContext = new KafkaConsumerCorrelationContext();
    private final KafkaTaskEventConsumer consumer =
            new KafkaTaskEventConsumer(correlationContext, meterRegistry);

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    void shouldRestorePropagatedCorrelationIdAndClearItAfterProcessing() {
        var correlationId = UUID.randomUUID().toString();
        var observedCorrelationId = new AtomicReference<String>();

        correlationContext.withCorrelationId(correlationId.getBytes(StandardCharsets.UTF_8),
                () -> observedCorrelationId.set(MDC.get("correlationId")));

        assertThat(observedCorrelationId.get()).isEqualTo(correlationId);
        assertThat(MDC.get("correlationId")).isNull();
    }

    @Test
    void shouldGenerateAndClearCorrelationIdWhenHeaderIsMissingOrInvalid() {
        var generatedForMissingHeader = new AtomicReference<String>();
        var generatedForInvalidHeader = new AtomicReference<String>();

        correlationContext.withCorrelationId(null,
                () -> generatedForMissingHeader.set(MDC.get("correlationId")));
        correlationContext.withCorrelationId("invalid".getBytes(StandardCharsets.UTF_8),
                () -> generatedForInvalidHeader.set(MDC.get("correlationId")));

        assertThat(UUID.fromString(generatedForMissingHeader.get()).toString())
                .isEqualTo(generatedForMissingHeader.get());
        assertThat(UUID.fromString(generatedForInvalidHeader.get()).toString())
                .isEqualTo(generatedForInvalidHeader.get());
        assertThat(MDC.get("correlationId")).isNull();
    }

    @Test
    void shouldClearCorrelationIdWhenProcessingFails() {
        assertThatThrownBy(() -> correlationContext.withCorrelationId(null,
                () -> {
                    throw new RuntimeException("processing failed");
                }))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("processing failed");

        assertThat(MDC.get("correlationId")).isNull();
    }

    @Test
    void shouldRecordSuccessfulProcessingWithoutDltMetric() {
        consumer.consumeNotifications("task created", null);

        assertThat(processedCounter("notifications", "success").count()).isEqualTo(1.0);
        assertThat(meterRegistry.find("todoapp.kafka.events.dlt").counter()).isNull();
    }

    @Test
    void shouldRecordFailedProcessingAndDltOutcome() {
        assertThatThrownBy(() -> consumer.consumeAudit("falha", null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Simulated processing failure");

        consumer.handleDlt("falha", "todo-tasks-dlt", null);

        assertThat(processedCounter("audit", "failure").count()).isEqualTo(1.0);
        assertThat(meterRegistry.find("todoapp.kafka.events.dlt")
                .tags("consumer", "audit", "event_type", "unknown")
                .counter()
                .count()).isEqualTo(1.0);
    }

    private io.micrometer.core.instrument.Counter processedCounter(String consumer, String outcome) {
        return meterRegistry.find("todoapp.kafka.events.processed")
                .tags("consumer", consumer, "outcome", outcome)
                .counter();
    }
}
