package com.banzak.todoapp.infrastructure.observability;

import com.banzak.todoapp.application.TaskOperationMetrics;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class MicrometerTaskOperationMetrics implements TaskOperationMetrics {

    private final Counter createdCounter;
    private final Counter updatedCounter;

    public MicrometerTaskOperationMetrics(MeterRegistry meterRegistry) {
        this.createdCounter = Counter.builder("todoapp.tasks.created")
                .tag("source", "api")
                .register(meterRegistry);
        this.updatedCounter = Counter.builder("todoapp.tasks.updated")
                .tag("source", "api")
                .register(meterRegistry);
    }

    @Override
    public void recordCreated() {
        createdCounter.increment();
    }

    @Override
    public void recordUpdated() {
        updatedCounter.increment();
    }
}
