package com.banzak.todoapp.infrastructure.messaging;

import java.time.LocalDateTime;

public record TaskEvent(
    String eventType,
    Long taskId,
    String title,
    String status,
    String priority,
    LocalDateTime timestamp
) {}
