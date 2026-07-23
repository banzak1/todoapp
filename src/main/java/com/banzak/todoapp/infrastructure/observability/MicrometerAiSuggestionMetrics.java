package com.banzak.todoapp.infrastructure.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class MicrometerAiSuggestionMetrics {

    private final Counter successCounter;
    private final Counter failureCounter;

    public MicrometerAiSuggestionMetrics(MeterRegistry meterRegistry) {
        this.successCounter = Counter.builder("todoapp.ai.suggestions")
                .tag("outcome", "success")
                .register(meterRegistry);
        this.failureCounter = Counter.builder("todoapp.ai.suggestions")
                .tag("outcome", "failure")
                .register(meterRegistry);
    }

    public void recordSuccess() {
        successCounter.increment();
    }

    public void recordFailure() {
        failureCounter.increment();
    }
}
