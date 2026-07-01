package com.banzak.todoapp.application;

import com.banzak.todoapp.domain.Task;

public interface TaskEventPublisher {
    void publishCreated(Task task);
    void publishUpdated(Task task);
    void publishDeleted(Long taskId);
}
