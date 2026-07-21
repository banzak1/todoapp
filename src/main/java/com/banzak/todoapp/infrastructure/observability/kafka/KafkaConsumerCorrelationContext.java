package com.banzak.todoapp.infrastructure.observability.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerCorrelationContext {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerCorrelationContext.class);
    private static final String MDC_KEY = "correlationId";

    public void withCorrelationId(byte[] headerValue, Runnable action) {
        if (!KafkaCorrelationId.isValid(headerValue)) {
            log.warn("Kafka message has no valid correlation ID; generating a new value");
        }

        MDC.put(MDC_KEY, KafkaCorrelationId.resolve(headerValue));
        try {
            action.run();
        } finally {
            MDC.remove(MDC_KEY);
        }
    }
}
