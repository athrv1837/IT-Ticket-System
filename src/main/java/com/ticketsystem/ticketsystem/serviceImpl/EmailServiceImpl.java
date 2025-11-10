package com.ticketsystem.ticketsystem.serviceImpl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.ticketsystem.ticketsystem.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.reset-url:http://localhost:8080/reset-password?token=}")
    private String resetPasswordBaseUrl;

    @Override
    public void sendPasswordResetEmail(String toEmail, String token) {
        try {
            String subject = "Password Reset Request – IT Support Ticket System";

            String resetLink = resetPasswordBaseUrl + token;

            String htmlContent = """
                    <div style="font-family: Arial, sans-serif; color: #333; background-color: #f9f9f9; padding: 20px;">
                        <h2 style="color: #007bff;">Password Reset Request</h2>
                        <p>Dear user,</p>
                        <p>We received a request to reset your password for your IT Support Ticket System account.</p>
                        <p>If you made this request, please click the button below to reset your password:</p>
                        <p style="text-align: center; margin: 30px 0;">
                            <a href="%s" style="background-color: #007bff; color: white; padding: 12px 20px; text-decoration: none; border-radius: 5px; display: inline-block;">
                                Reset Password
                            </a>
                        </p>
                        <p>If you did not request this, you can safely ignore this email. This link will expire in 15 minutes.</p>
                        <hr/>
                        <p style="font-size: 12px; color: #666;">© %d IT Support Ticket System. All rights reserved.</p>
                    </div>
                    """.formatted(resetLink, java.time.Year.now().getValue());

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email to " + toEmail + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void sendWelcomeEmail(String toEmail, String username) {
        try {
            String subject = "Welcome to IT Support Ticket System!";

            String htmlContent = """
                    <div style="font-family: Arial, sans-serif; color: #333; background-color: #f9f9f9; padding: 20px;">
                        <h2 style="color: #007bff;">Welcome, %s!</h2>
                        <p>Your account has been successfully created in the IT Support Ticket System.</p>
                        <p>You can now log in and start creating or managing tickets.</p>
                        <p style="margin-top: 20px;">Thank you for joining us!</p>
                        <hr/>
                        <p style="font-size: 12px; color: #666;">© %d IT Support Ticket System.</p>
                    </div>
                    """.formatted(username, java.time.Year.now().getValue());

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send welcome email: " + e.getMessage(), e);
        }
    }
}
