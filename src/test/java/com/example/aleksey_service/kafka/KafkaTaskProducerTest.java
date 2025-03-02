package com.example.aleksey_service.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class KafkaTaskProducerTest {
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private KafkaTaskProducer kafkaTaskProducer;

    @Test
    void sendSuccess() {
        List<Object> messages = List.of("Message1", "Message2");

        kafkaTaskProducer.send(messages);

        verify(kafkaTemplate, times(1)).sendDefault("Message1");
        verify(kafkaTemplate, times(1)).sendDefault("Message2");
    }

    @Test
    void sendFail_ExceptionHandling() {
        List<Object> messages = List.of("Message1");

        doThrow(new RuntimeException("Kafka error")).when(kafkaTemplate).sendDefault("Message1");

        assertThrows(RuntimeException.class, () -> kafkaTaskProducer.send(messages));

        verify(kafkaTemplate, times(1)).sendDefault("Message1");
    }
}
