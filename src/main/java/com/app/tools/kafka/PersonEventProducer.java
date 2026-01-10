package com.app.tools.kafka;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@EnableScheduling
@ConditionalOnProperty(name = "spring.kafka.producer.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class PersonEventProducer {

    private final KafkaTemplate<String, PersonEvent> kafkaTemplate;
    private static final String TOPIC = "person-topic";
    private final AtomicInteger counter = new AtomicInteger(1);

    @Scheduled(fixedRate = 5000) // every 5 seconds
    public void sendPersonEvent() {
        PersonEvent event = PersonEvent.builder()
                .id(counter.getAndIncrement())
                .name("Person-" + counter)
                .email("person" + counter + "@example.com")
                .build();

        kafkaTemplate.send(TOPIC, String.valueOf(event.getId()), event);
        System.out.println("Sent: " + event);
    }
}