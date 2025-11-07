package com.ticketsystem.ticketsystem.service;

public interface EmailService {
    void sendPasswordResetEmail(String to, String token);
}