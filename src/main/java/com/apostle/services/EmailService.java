package com.apostle.services;

import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    void sendPasswordResetEmail(String toEmail, String token) throws MessagingException;
}
