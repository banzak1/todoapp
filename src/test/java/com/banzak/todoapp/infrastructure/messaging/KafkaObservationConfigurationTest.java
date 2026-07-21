package com.banzak.todoapp.infrastructure.messaging;

import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.kafka.listener.auto-startup=false")
@AutoConfigureObservability
class KafkaObservationConfigurationTest {

    @Autowired
    private KafkaTemplate<?, ?> kafkaTemplate;

    @Autowired
    private ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory;

    @Autowired
    private ObservationRegistry observationRegistry;

    @Test
    void shouldEnableObservationsForKafkaTemplateAndListeners() {
        assertThat(ReflectionTestUtils.getField(kafkaTemplate, "observationEnabled")).isEqualTo(true);
        assertThat(ReflectionTestUtils.getField(kafkaTemplate, "observationRegistry"))
                .isSameAs(observationRegistry);
        assertThat(kafkaListenerContainerFactory.getContainerProperties().isObservationEnabled()).isTrue();
        assertThat(kafkaListenerContainerFactory.getContainerProperties().getObservationRegistry())
                .isSameAs(observationRegistry);
    }
}
