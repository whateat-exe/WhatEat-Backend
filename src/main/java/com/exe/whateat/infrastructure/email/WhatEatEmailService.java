package com.exe.whateat.infrastructure.email;

import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
public class WhatEatEmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String emailSender;

    public WhatEatEmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendMail(String recipient, String body, String subject) {
        final MimeMessagePreparator messagePreparator = mimeMessage -> {
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            mimeMessage.setFrom(new InternetAddress(emailSender));
            mimeMessage.setSubject(subject);
            mimeMessage.setContent(body, "text/html; charset=utf-8");
        };
        javaMailSender.send(messagePreparator);
    }
}
