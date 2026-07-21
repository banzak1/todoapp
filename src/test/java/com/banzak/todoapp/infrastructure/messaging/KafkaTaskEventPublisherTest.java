package com.banzak.todoapp.infrastructure.messaging;

import com.banzak.todoapp.domain.Task;
import com.banzak.todoapp.domain.TaskPriority;
import com.banzak.todoapp.domain.TaskStatus;
import com.banzak.todoapp.infrastructure.observability.kafka.KafkaCorrelationId;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class KafkaTaskEventPublisherTest {

    private final KafkaTemplate<String, Object> kafkaTemplate = mock(KafkaTemplate.class);
    private final SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
    private final KafkaTaskEventPublisher publisher =
            new KafkaTaskEventPublisher(kafkaTemplate, meterRegistry);

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    void shouldPropagateCorrelationIdAndRecordSuccessfulPublication() {
        var correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);
        var future = new CompletableFuture<SendResult<String, Object>>();
        when(kafkaTemplate.send(anyProducerRecord())).thenReturn(future);

        publisher.publishCreated(task());
        future.complete(null);

        var recordCaptor = producerRecordCaptor();
        org.mockito.Mockito.verify(kafkaTemplate).send(recordCaptor.capture());
        var header = recordCaptor.getValue().headers().lastHeader(KafkaCorrelationId.HEADER);

        assertThat(new String(header.value(), StandardCharsets.UTF_8)).isEqualTo(correlationId);
        assertThat(recordCaptor.getValue().value()).isInstanceOf(TaskEvent.class);
        assertThat(counter("CREATED", "success").count()).isEqualTo(1.0);
    }

    @Test
    void shouldGenerateCorrelationIdWhenMdcIsMissing() {
        var future = new CompletableFuture<SendResult<String, Object>>();
        when(kafkaTemplate.send(anyProducerRecord())).thenReturn(future);

        publisher.publishUpdated(task());
        future.complete(null);

        var recordCaptor = producerRecordCaptor();
        org.mockito.Mockito.verify(kafkaTemplate).send(recordCaptor.capture());
        var header = recordCaptor.getValue().headers().lastHeader(KafkaCorrelationId.HEADER);
        var correlationId = new String(header.value(), StandardCharsets.UTF_8);

        assertThat(UUID.fromString(correlationId).toString()).isEqualTo(correlationId);
        assertThat(counter("UPDATED", "success").count()).isEqualTo(1.0);
    }

    @Test
    void shouldRecordFailedPublicationWhenKafkaFutureFails() {
        var future = new CompletableFuture<SendResult<String, Object>>();
        when(kafkaTemplate.send(anyProducerRecord())).thenReturn(future);

        publisher.publishCreated(task());
        future.completeExceptionally(new RuntimeException("Kafka unavailable"));

        assertThat(counter("CREATED", "failure").count()).isEqualTo(1.0);
    }

    @Test
    void shouldRecordFailedPublicationWhenKafkaSendThrows() {
        when(kafkaTemplate.send(anyProducerRecord()))
                .thenThrow(new RuntimeException("Kafka unavailable"));

        assertThatThrownBy(() -> publisher.publishCreated(task()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Kafka unavailable");

        assertThat(counter("CREATED", "failure").count()).isEqualTo(1.0);
    }

    private Task task() {
        return Task.builder()
                .id(1L)
                .title("Task")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.MEDIUM)
                .build();
    }

    private io.micrometer.core.instrument.Counter counter(String eventType, String outcome) {
        return meterRegistry.find("todoapp.kafka.events.published")
                .tags("event_type", eventType, "outcome", outcome)
                .counter();
    }

    @SuppressWarnings("unchecked")
    private static ArgumentCaptor<ProducerRecord<String, Object>> producerRecordCaptor() {
        return ArgumentCaptor.forClass((Class<ProducerRecord<String, Object>>) (Class<?>) ProducerRecord.class);
    }

    @SuppressWarnings("unchecked")
    private static ProducerRecord<String, Object> anyProducerRecord() {
        return any(ProducerRecord.class);
    }
}
