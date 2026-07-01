package com.banzak.todoapp.infrastructure.messaging;

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

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            dltTopicSuffix = "-dlt"
    )
    @KafkaListener(topics = "todo-tasks", groupId = "todoapp-audit")
    public void consumeAudit(String message) {
        log.info("[AUDIT LOG] Received event from Kafka: {}", message);
        
        // Simulação de falha para testar o Dead Letter Topic (DLT)
        if (message.contains("fail") || message.contains("falha")) {
            log.warn("[AUDIT LOG] Simulating error. Message will be retried...");
            throw new RuntimeException("Simulated processing failure for message: " + message);
        }
    }

    @DltHandler
    public void handleDlt(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("[DLT LOG] Message failed all retries and landed in DLT topic '{}': {}", topic, message);
    }

    @KafkaListener(topics = "todo-tasks", groupId = "todoapp-notifications")
    public void consumeNotifications(String message) {
        log.info("[NOTIFICATION LOG] Simulating notification for event: {}", message);
    }
}
