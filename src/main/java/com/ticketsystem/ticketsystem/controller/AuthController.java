package com.ticketsystem.ticketsystem.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ticketsystem.ticketsystem.DTO.AuthRequest;
import com.ticketsystem.ticketsystem.DTO.AuthResponse;
import com.ticketsystem.ticketsystem.DTO.PasswordResetDTO;
import com.ticketsystem.ticketsystem.DTO.PasswordResetRequestDTO;
import com.ticketsystem.ticketsystem.DTO.UserDTO;
import com.ticketsystem.ticketsystem.DTO.UserResgistrationDTO;
import com.ticketsystem.ticketsystem.entity.User;
import com.ticketsystem.ticketsystem.security.Jwtutil;
import com.ticketsystem.ticketsystem.service.EmailService;
import com.ticketsystem.ticketsystem.service.UserService;
import com.ticketsystem.ticketsystem.serviceImpl.UserDetailsServiceImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private Jwtutil jwtutil;

    @Autowired
    private EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserResgistrationDTO userDTO) {
        User user = userDetailsService.createUser(userDTO);
        emailService.sendWelcomeEmail(user.getEmail(), user.getUsername());
        return ResponseEntity.ok(Map.of(
                "message", "User registered successfully! Please log in to continue.",
                "username", user.getUsername(),
                "role", user.getRole().toString()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(),
                        authRequest.getPassword()));
        UserDTO user = userService.getUserByUserame(authRequest.getUsername());
        String token = jwtutil.generateToken(new UsernamePasswordAuthenticationToken(user.getUsername(), null));
        AuthResponse authResponse = new AuthResponse();
        authResponse.setUsername(user.getUsername());
        authResponse.setToken(token);
        authResponse.setRole(user.getRole().toString());
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> requestPasswordReset(@Valid @RequestBody PasswordResetRequestDTO request) {
        String token = userDetailsService.createPasswordResetToken(request.getEmail());
        emailService.sendPasswordResetEmail(request.getEmail(), token);
        return ResponseEntity.ok().body(Map.of("message", "Password reset email sent successfully"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetDTO request) {
        System.out.println(request);
        userDetailsService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok().body(Map.of("message", "Password reset successfully"));
    }
}
