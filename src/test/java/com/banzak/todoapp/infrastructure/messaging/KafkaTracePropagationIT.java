package com.banzak.todoapp.infrastructure.messaging;

import io.micrometer.tracing.Tracer;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.test.context.EmbeddedKafka;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "management.tracing.enabled=true",
        "management.otlp.tracing.export.enabled=false",
        "management.tracing.sampling.probability=1.0",
        "management.tracing.opentelemetry.export.schedule-delay=10ms",
        "spring.kafka.listener.auto-startup=true"
})
@AutoConfigureObservability
@EmbeddedKafka(partitions = 1, topics = KafkaTracePropagationIT.TOPIC)
@Import(KafkaTracePropagationIT.TraceTestConfiguration.class)
class KafkaTracePropagationIT {

    static final String TOPIC = "trace-propagation";
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ProducerFactory<String, String> producerFactory;

    @Autowired
    private Tracer tracer;

    @Autowired
    private RecordingSpanExporter spanExporter;

    @Autowired
    private TraceTestListener listener;

    @BeforeEach
    void resetTestState() {
        spanExporter.clear();
        listener.reset();
    }

    @Test
    void shouldKeepProducerAndConsumerInTheSameTrace_whenTraceparentIsPropagated() throws Exception {
        var parentSpan = tracer.nextSpan().name("test.kafka.publish").start();
        try (var ignored = tracer.withSpan(parentSpan)) {
            kafkaTemplate.send(TOPIC, "with-traceparent").get(TIMEOUT.toSeconds(), TimeUnit.SECONDS);
        } finally {
            parentSpan.end();
        }

        assertThat(listener.awaitMessage(TIMEOUT)).isTrue();
        var spans = awaitConsumerSpan();

        assertThat(listener.traceparent()).isNotBlank();
        assertThat(producerSpan(spans).getTraceId()).isEqualTo(consumerSpan(spans).getTraceId());
    }

    @Test
    void shouldCreateNewConsumerTrace_whenTraceparentIsMissing() throws Exception {
        var templateWithoutObservation = new KafkaTemplate<>(producerFactory);
        templateWithoutObservation.setObservationEnabled(false);
        templateWithoutObservation.send(new ProducerRecord<>(TOPIC, "without-traceparent"))
                .get(TIMEOUT.toSeconds(), TimeUnit.SECONDS);

        assertThat(listener.awaitMessage(TIMEOUT)).isTrue();
        var spans = awaitConsumerSpan();

        assertThat(listener.traceparent()).isNull();
        assertThat(consumerSpan(spans).getParentSpanContext().isValid()).isFalse();
    }

    private List<SpanData> awaitConsumerSpan() throws InterruptedException {
        var deadline = System.nanoTime() + TIMEOUT.toNanos();
        List<SpanData> spans = List.of();
        while (System.nanoTime() < deadline) {
            spans = spanExporter.spans();
            if (spans.stream().anyMatch(span -> span.getKind() == SpanKind.CONSUMER)) {
                return spans;
            }
            Thread.sleep(25);
        }
        return spans;
    }

    private SpanData producerSpan(List<SpanData> spans) {
        return spans.stream()
                .filter(span -> span.getKind() == SpanKind.PRODUCER)
                .findFirst()
                .orElseThrow();
    }

    private SpanData consumerSpan(List<SpanData> spans) {
        return spans.stream()
                .filter(span -> span.getKind() == SpanKind.CONSUMER)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No consumer span captured: " + spans));
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class TraceTestConfiguration {

        @Bean
        RecordingSpanExporter recordingSpanExporter() {
            return new RecordingSpanExporter();
        }

        @Bean
        TraceTestListener traceTestListener() {
            return new TraceTestListener();
        }
    }

    static class TraceTestListener {

        private volatile CountDownLatch received = new CountDownLatch(1);
        private volatile String traceparent;

        @KafkaListener(topics = TOPIC, groupId = "trace-propagation-test")
        void consume(ConsumerRecord<String, String> record) {
            var header = record.headers().lastHeader("traceparent");
            traceparent = header == null ? null : new String(header.value());
            received.countDown();
        }

        void reset() {
            traceparent = null;
            received = new CountDownLatch(1);
        }

        boolean awaitMessage(Duration timeout) throws InterruptedException {
            return received.await(timeout.toMillis(), TimeUnit.MILLISECONDS);
        }

        String traceparent() {
            return traceparent;
        }
    }

    static class RecordingSpanExporter implements SpanExporter {

        private final List<SpanData> spans = new CopyOnWriteArrayList<>();

        @Override
        public CompletableResultCode export(Collection<SpanData> exportedSpans) {
            spans.addAll(exportedSpans);
            return CompletableResultCode.ofSuccess();
        }

        @Override
        public CompletableResultCode flush() {
            return CompletableResultCode.ofSuccess();
        }

        @Override
        public CompletableResultCode shutdown() {
            return CompletableResultCode.ofSuccess();
        }

        void clear() {
            spans.clear();
        }

        List<SpanData> spans() {
            return new ArrayList<>(spans);
        }
    }
}
