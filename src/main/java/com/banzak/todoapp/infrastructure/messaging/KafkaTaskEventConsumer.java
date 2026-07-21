package com.banzak.todoapp.infrastructure.messaging;

import com.banzak.todoapp.infrastructure.observability.kafka.KafkaConsumerCorrelationContext;
import com.banzak.todoapp.infrastructure.observability.kafka.KafkaCorrelationId;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
public class KafkaTaskEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaTaskEventConsumer.class);
    private final KafkaConsumerCorrelationContext correlationContext;
    private final MeterRegistry meterRegistry;

    public KafkaTaskEventConsumer(
            KafkaConsumerCorrelationContext correlationContext,
            MeterRegistry meterRegistry) {
        this.correlationContext = correlationContext;
        this.meterRegistry = meterRegistry;
    }

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            dltTopicSuffix = "-dlt"
    )
    @KafkaListener(topics = "todo-tasks", groupId = "todoapp-audit")
    public void consumeAudit(
            String message,
            @Header(name = KafkaCorrelationId.HEADER, required = false) byte[] correlationId) {
        process("audit", correlationId, () -> {
            log.info("[AUDIT LOG] Received event from Kafka: {}", message);

            if (message.contains("fail") || message.contains("falha")) {
                log.warn("[AUDIT LOG] Simulating error. Message will be retried...");
                throw new RuntimeException("Simulated processing failure for message: " + message);
            }
        });
    }

    @DltHandler
    public void handleDlt(
            String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(name = KafkaCorrelationId.HEADER, required = false) byte[] correlationId) {
        correlationContext.withCorrelationId(correlationId, () -> {
            log.error("[DLT LOG] Message failed all retries and landed in DLT topic '{}': {}", topic, message);
            Counter.builder("todoapp.kafka.events.dlt")
                    .tags("consumer", "audit", "event_type", "unknown")
                    .register(meterRegistry)
                    .increment();
        });
    }

    @KafkaListener(topics = "todo-tasks", groupId = "todoapp-notifications")
    public void consumeNotifications(
            String message,
            @Header(name = KafkaCorrelationId.HEADER, required = false) byte[] correlationId) {
        process("notifications", correlationId,
                () -> log.info("[NOTIFICATION LOG] Simulating notification for event: {}", message));
    }

    private void process(String consumer, byte[] correlationId, Runnable action) {
        correlationContext.withCorrelationId(correlationId, () -> {
            try {
                action.run();
                recordProcessed(consumer, "success");
            } catch (RuntimeException exception) {
                recordProcessed(consumer, "failure");
                throw exception;
            }
        });
    }

    private void recordProcessed(String consumer, String outcome) {
        Counter.builder("todoapp.kafka.events.processed")
                .tags("consumer", consumer, "outcome", outcome)
                .register(meterRegistry)
                .increment();
    }
}
