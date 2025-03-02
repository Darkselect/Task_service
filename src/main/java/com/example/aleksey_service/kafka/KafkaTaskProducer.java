package com.example.aleksey_service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaTaskProducer {
    private final KafkaTemplate kafkaTemplate;

    public void send(List<Object> messages) {
        try {
            log.info("Отправка сообщений в Kafka: {}", messages.size());

            messages.forEach(message -> kafkaTemplate.sendDefault(message));

        } catch (Exception ex) {
            log.error("Ошибка при отправке сообщений в Kafka: {} ", messages.size(), ex);
            throw ex;
        }
    }
}
