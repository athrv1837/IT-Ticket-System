package com.ticketsystem.ticketsystem.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.ticketsystem.ticketsystem.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private final JavaMailSender mailSender;
    private final String fromEmail = "noreply@ticketsystem.com"; // Configure in application.properties

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendPasswordResetEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Password Reset Request");
        message.setText("To reset your password, click the following link: "
                + "http://localhost:8080/api/auth/reset-password?token=" + token
                + "\n\nThis link will expire in 15 minutes.");

        mailSender.send(message);
    }
}