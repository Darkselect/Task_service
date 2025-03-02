package com.example.aleksey_service.service;

import com.example.aleksey_service.dto.KafkaDto;
import com.example.aleksey_service.dto.TaskDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    @Value("${mail.username}")
    private String mailTo;
    @Value("${mail.subjectForEmail}")
    private String subjectForEmail;

    private final JavaMailSender mailSender;

    public void sendMail(KafkaDto kafkaDto) {
        try {
            String forEmail = String.format("Задача: %s\nОписание: %s\nID пользователя: %d\nСтатус: %s",
                    kafkaDto.getTitle(), kafkaDto.getDescription(), kafkaDto.getUserId(), kafkaDto.getStatus());

            log.info("Sending email: {}, to {}", subjectForEmail, mailTo);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(mailTo);
            helper.setSubject(subjectForEmail);
            helper.setText(forEmail);

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Failed to send email {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
