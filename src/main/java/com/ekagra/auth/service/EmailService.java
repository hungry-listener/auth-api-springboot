package com.ekagra.auth.service;

import java.io.IOException;

import java.nio.charset.StandardCharsets;


import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.ekagra.auth.exceptions.EmailSendException;
import com.ekagra.auth.properties.EmailProperties;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailProperties emailProperties;
    private final ResourceLoader resourceLoader;

    // Constructor-based injection
    public EmailService(JavaMailSender mailSender, EmailProperties emailProperties, ResourceLoader resourceLoader) {
        this.mailSender = mailSender;
        this.emailProperties = emailProperties;
        this.resourceLoader = resourceLoader;
    }

    public String loadActivationEmailTemplate() throws IOException {
        Resource resource = resourceLoader.getResource(emailProperties.getVerificationTemplatePath());
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    public String loadPasswordResetEmailTemplate() throws IOException {
        Resource resource = resourceLoader.getResource(emailProperties.getPasswordResetTemplatePath());
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
    
    
    public void sendActivationEmail(String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailProperties.getSender()); // or your configured sender
            message.setTo(to);
            message.setSubject(emailProperties.getActivationEmailSubject());
            String activationLink = emailProperties.getActivationBaseUrl() + "?token=" + token;
            String body = loadActivationEmailTemplate().replace("{link}", activationLink);
            message.setText(body);

            mailSender.send(message);
    
        } catch (Exception e) {
            // Optionally log the exception
            throw new EmailSendException("Failed to send email to: " + to, e);
        }
    }

    public void sendPasswordResetEmail(String to, String username, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailProperties.getSender()); 
            message.setTo(to);
            message.setSubject(emailProperties.getPasswordResetEmailSubject());
            String activationLink = emailProperties.getPasswordResetBaseUrl() + "?resetToken=" + token;
            String body = loadPasswordResetEmailTemplate().replace("{link}", activationLink).replace("username", username);
            message.setText(body);

            mailSender.send(message);
    
        } catch (Exception e) {
            // Optionally log the exception
            throw new EmailSendException("Failed to send email to: " + to, e);
        }
    }
}
