package com.ecommerce.controller;

import com.ecommerce.model.EmailRequest;
import com.ecommerce.model.PasswordResetToken;
import com.ecommerce.model.ResetPasswordRequest;
import com.ecommerce.repository.PasswordResetTokenRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final PasswordResetTokenRepository tokenRepo;
    private final EmailService emailService;
    private final UserRepository userRepo; // Your existing user repo

    public AuthController(PasswordResetTokenRepository tokenRepo, EmailService emailService, UserRepository userRepo) {
        this.tokenRepo = tokenRepo;
        this.emailService = emailService;
        this.userRepo = userRepo;
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestBody EmailRequest request) {
        var user = userRepo.findByEmail(request.getEmail());
        if (user.isEmpty()) {
            return "User not found";
        }

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setEmail(request.getEmail());
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        tokenRepo.save(resetToken);

        String link = "http://localhost:3000/resetpassword?token=" + token;
        emailService.sendEmail(request.getEmail(), "Reset Password", "Click to reset: " + link);

        return "Password reset link sent!";
    }

    @PostMapping("/reset-password")
    @Transactional
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody ResetPasswordRequest request) {
        var resetToken = tokenRepo.findByToken(request.getToken());
        if (resetToken.isEmpty() || resetToken.get().isExpired()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid or expired token"));
        }

        var user = userRepo.findByEmail(resetToken.get().getEmail());
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "User not found"));
        }

        user.get().setPassword(request.getPassword()); // hash in real code
        userRepo.save(user.get());
        tokenRepo.deleteByToken(request.getToken());

        return ResponseEntity.ok(Map.of("message", "Password reset successful!"));
    }

}

