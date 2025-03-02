package com.example.aleksey_service.kafka;

import com.example.aleksey_service.dto.KafkaDto;
import com.example.aleksey_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaTaskConsumer {
    private final EmailService emailService;

    @KafkaListener(topics = "${kafka.task-topic}", groupId = "${kafka.consumer-group-id}", containerFactory = "taskKafkaListenerContainerFactory")
    public void consume(@Payload List<KafkaDto> messages, Acknowledgment ack) {
        try {
            log.info("Получены сообщения из Kafka: {}", messages);
            ack.acknowledge();
            messages.forEach(emailService::sendMail);

        } catch (Exception e) {
            log.error("Ошибка обработки сообщений: ", e);
        }
    }
}
