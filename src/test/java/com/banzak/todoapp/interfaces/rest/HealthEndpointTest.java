package com.banzak.todoapp.interfaces.rest;

import com.banzak.todoapp.TodoappApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@DisplayName("Actuator health endpoints")
class HealthEndpointTest {

    @Test
    @DisplayName("should expose safe health probes and hide administrative endpoints")
    void shouldExposeSafeHealthProbes_whenApplicationIsRunning() throws IOException, InterruptedException {
        try (ConfigurableApplicationContext context = new SpringApplicationBuilder(TodoappApplication.class)
                .properties(
                        "server.port=0",
                        "spring.kafka.listener.auto-startup=false"
                )
                .run()) {
            var port = context.getEnvironment().getProperty("local.server.port", Integer.class);
            var client = HttpClient.newHttpClient();

            var liveness = get(client, port, "/actuator/health/liveness");
            var readiness = get(client, port, "/actuator/health/readiness");
            var health = get(client, port, "/actuator/health");
            var environment = get(client, port, "/actuator/env");

            org.assertj.core.api.Assertions.assertThat(liveness.statusCode()).isEqualTo(200);
            org.assertj.core.api.Assertions.assertThat(liveness.body()).contains("\"status\":\"UP\"");
            org.assertj.core.api.Assertions.assertThat(readiness.statusCode()).isEqualTo(200);
            org.assertj.core.api.Assertions.assertThat(readiness.body()).contains("\"status\":\"UP\"");
            org.assertj.core.api.Assertions.assertThat(health.body()).doesNotContain("components");
            org.assertj.core.api.Assertions.assertThat(environment.statusCode()).isEqualTo(404);
        }
    }

    private HttpResponse<String> get(HttpClient client, Integer port, String path)
            throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).GET().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
