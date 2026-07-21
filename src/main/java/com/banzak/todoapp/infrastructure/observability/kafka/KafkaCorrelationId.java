package com.banzak.todoapp.infrastructure.observability.kafka;

import java.util.UUID;
import java.nio.charset.StandardCharsets;
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

    public static String resolve(byte[] headerValue) {
        if (!isValid(headerValue)) {
            return UUID.randomUUID().toString();
        }
        return new String(headerValue, StandardCharsets.UTF_8);
    }

    public static boolean isValid(byte[] headerValue) {
        if (headerValue == null || headerValue.length == 0) {
            return false;
        }
        try {
            UUID.fromString(new String(headerValue, StandardCharsets.UTF_8));
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }
}
