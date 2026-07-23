package com.banzak.todoapp.infrastructure.messaging;

import com.banzak.todoapp.application.TaskEventPublisher;
import com.banzak.todoapp.domain.Task;
import com.banzak.todoapp.infrastructure.observability.kafka.KafkaCorrelationId;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.nio.charset.StandardCharsets;
import org.apache.kafka.clients.producer.ProducerRecord;

@Component
public class KafkaTaskEventPublisher implements TaskEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final MeterRegistry meterRegistry;
    private static final String TOPIC = "todo-tasks";

    public KafkaTaskEventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            MeterRegistry meterRegistry) {
        this.kafkaTemplate = kafkaTemplate;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void publishCreated(Task task) {
        var event = new TaskEvent(
                "CREATED",
                task.getId(),
                task.getTitle(),
                task.getStatus() != null ? task.getStatus().name() : null,
                task.getPriority() != null ? task.getPriority().name() : null,
                LocalDateTime.now()
        );
        publish(event, task.getId().toString());
    }

    @Override
    public void publishUpdated(Task task) {
        var event = new TaskEvent(
                "UPDATED",
                task.getId(),
                task.getTitle(),
                task.getStatus() != null ? task.getStatus().name() : null,
                task.getPriority() != null ? task.getPriority().name() : null,
                LocalDateTime.now()
        );
        publish(event, task.getId().toString());
    }

    @Override
    public void publishDeleted(Long taskId) {
        var event = new TaskEvent(
                "DELETED",
                taskId,
                null,
                null,
                null,
                LocalDateTime.now()
        );
        publish(event, taskId.toString());
    }

    private void publish(TaskEvent event, String key) {
        var record = new ProducerRecord<String, Object>(TOPIC, key, event);
        record.headers().add(
                KafkaCorrelationId.HEADER,
                KafkaCorrelationId.resolve().getBytes(StandardCharsets.UTF_8));

        try {
            kafkaTemplate.send(record)
                    .whenComplete((result, exception) -> recordPublishOutcome(event.eventType(), exception));
        } catch (RuntimeException exception) {
            recordPublishOutcome(event.eventType(), exception);
            throw exception;
        }
    }

    private void recordPublishOutcome(String eventType, Throwable exception) {
        Counter.builder("todoapp.kafka.events.published")
                .tags("event_type", eventType, "outcome", exception == null ? "success" : "failure")
                .register(meterRegistry)
                .increment();
    }
}
