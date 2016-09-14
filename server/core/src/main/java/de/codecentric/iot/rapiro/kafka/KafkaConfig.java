package de.codecentric.iot.rapiro.kafka;

import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Little helper to setup kafka.
 */
@Configuration
public class KafkaConfig {

    @PostConstruct
    public void initKafka() {

    }

}
