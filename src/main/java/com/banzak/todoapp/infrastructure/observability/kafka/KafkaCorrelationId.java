package com.banzak.todoapp.infrastructure.observability.kafka;

import java.util.UUID;
import org.slf4j.MDC;

public final class KafkaCorrelationId {

    public static final String HEADER = "X-Correlation-ID";
    private static final String MDC_KEY = "correlationId";

    private KafkaCorrelationId() {
    }

    public static String resolve() {
        var correlationId = MDC.get(MDC_KEY);
        if (correlationId == null || correlationId.isBlank()) {
            return UUID.randomUUID().toString();
        }
        return correlationId;
    }
}
