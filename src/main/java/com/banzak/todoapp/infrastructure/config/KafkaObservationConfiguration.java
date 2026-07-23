package com.banzak.todoapp.infrastructure.config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class KafkaObservationConfiguration {

    @Bean
    SmartInitializingSingleton kafkaObservationConfigurer(
            KafkaTemplate<?, ?> kafkaTemplate,
            ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory,
            ObservationRegistry observationRegistry) {
        return () -> {
            kafkaTemplate.setObservationEnabled(true);
            kafkaTemplate.setObservationRegistry(observationRegistry);
            kafkaListenerContainerFactory.getContainerProperties().setObservationEnabled(true);
            kafkaListenerContainerFactory.getContainerProperties().setObservationRegistry(observationRegistry);
        };
    }
}
