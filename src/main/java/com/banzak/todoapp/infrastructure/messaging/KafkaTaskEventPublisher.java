package com.banzak.todoapp.infrastructure.messaging;

import com.banzak.todoapp.application.TaskEventPublisher;
import com.banzak.todoapp.domain.Task;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class KafkaTaskEventPublisher implements TaskEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "todo-tasks";

    public KafkaTaskEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
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
        kafkaTemplate.send(TOPIC, task.getId().toString(), event);
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
        kafkaTemplate.send(TOPIC, task.getId().toString(), event);
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
        kafkaTemplate.send(TOPIC, taskId.toString(), event);
    }
}
