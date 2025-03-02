package com.example.aleksey_service.kafka;

import com.example.aleksey_service.dto.KafkaDto;
import com.example.aleksey_service.entity.TaskStatus;
import com.example.aleksey_service.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class KafkaTaskConsumerTest {
    @Mock
    private EmailService emailService;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private KafkaTaskConsumer kafkaTaskConsumer;


    @Test
    void consumeSuccess() {
        List<KafkaDto> messages = List.of(
                new KafkaDto(1L, "Title1", "Desc1", 2L, TaskStatus.UPDATED),
                new KafkaDto(2L, "Title2", "Desc2", 3L, TaskStatus.CREATED)
        );

        kafkaTaskConsumer.consume(messages, acknowledgment);

        verify(emailService, times(1)).sendMail(messages.get(0));
        verify(emailService, times(1)).sendMail(messages.get(1));
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void consume_Fail_ExceptionHandling() {
        List<KafkaDto> messages = List.of(new KafkaDto(1L, "Title1", "Desc1", 2L, TaskStatus.UPDATED));

        doThrow(new RuntimeException("Email error")).when(emailService).sendMail(any());

        kafkaTaskConsumer.consume(messages, acknowledgment);

        verify(emailService, times(1)).sendMail(messages.get(0));
        verify(acknowledgment, times(1)).acknowledge();
    }
}
