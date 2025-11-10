package com.ticketsystem.ticketsystem.service;

public interface EmailService {
    void sendPasswordResetEmail(String to, String token);
    void sendWelcomeEmail(String toEmail, String username);
}