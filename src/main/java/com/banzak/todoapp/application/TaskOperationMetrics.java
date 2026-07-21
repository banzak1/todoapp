package com.banzak.todoapp.application;

public interface TaskOperationMetrics {

    void recordCreated();

    void recordUpdated();
}
