package com.example.aleksey_service.service;

import com.example.aleksey_service.dto.KafkaDto;
import com.example.aleksey_service.dto.TaskDto;
import com.example.aleksey_service.entity.TaskStatus;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {
    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "mailTo", "test@mail.com");
        ReflectionTestUtils.setField(emailService, "subjectForEmail", "Task Update Notification");
    }

    @Test
    void sendMailSuccess() {
        KafkaDto kafkaDto = KafkaDto.builder()
                .id(1L)
                .title("Test Title")
                .description("Test Description")
                .userId(2L)
                .status(TaskStatus.UPDATED)
                .build();

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendMail(kafkaDto);

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void sendMail_Fail_MessagingException() {
        KafkaDto kafkaDto = KafkaDto.builder()
                .id(1L)
                .title("Test Title")
                .description("Test Description")
                .userId(2L)
                .status(TaskStatus.UPDATED)
                .build();

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Failed to send")).when(mailSender).send(mimeMessage);

        assertThrows(RuntimeException.class, () -> emailService.sendMail(kafkaDto));

        verify(mailSender, times(1)).send(mimeMessage);
    }
}
