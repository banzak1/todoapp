package com.banzak.todoapp.interfaces.rest.observability;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.boot.logging.logback.StructuredLogEncoder;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.mock.env.MockEnvironment;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Structured logging configuration")
class StructuredLoggingConfigurationTest {

    @Test
    @DisplayName("should select Logstash JSON only for the production profile")
    void shouldSelectLogstashJson_onlyForProductionProfile() throws Exception {
        var propertySourceLoader = new YamlPropertySourceLoader();
        var resourceLoader = new DefaultResourceLoader();
        var productionProperties = propertySourceLoader
                .load("production", resourceLoader.getResource("classpath:application-production.yml"))
                .getFirst();
        var defaultProperties = propertySourceLoader
                .load("default", resourceLoader.getResource("classpath:application.yml"))
                .getFirst();

        assertThat(productionProperties.getProperty("logging.structured.format.console")).isEqualTo("logstash");
        assertThat(defaultProperties.getProperty("logging.structured.format.console")).isNull();
    }

    @Test
    @DisplayName("should include correlation ID from MDC in a Logstash JSON event")
    void shouldIncludeCorrelationId_whenMdcContainsIt() {
        var correlationId = UUID.randomUUID().toString();
        var encoder = new StructuredLogEncoder();
        var loggerContext = new LoggerContext();
        loggerContext.putObject(Environment.class.getName(), new MockEnvironment());
        encoder.setContext(loggerContext);
        encoder.setFormat("logstash");
        encoder.setCharset(StandardCharsets.UTF_8);
        encoder.start();

        try {
            var event = new LoggingEvent();
            event.setLevel(Level.INFO);
            event.setLoggerName("com.banzak.todoapp.StructuredLoggingConfigurationTest");
            event.setMessage("task created");
            event.setMDCPropertyMap(Map.of("correlationId", correlationId));
            event.setTimeStamp(0L);

            var json = new String(encoder.encode(event), StandardCharsets.UTF_8);

            assertThat(json).contains("\"correlationId\":\"" + correlationId + "\"");
            assertThat(json).doesNotContain("password", "prompt", "payload");
        } finally {
            encoder.stop();
        }
    }
}
