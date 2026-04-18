package com.capsule.service;

import com.capsule.model.Capsule;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${capsule.base-url}")
    private String baseUrl;

    public void sendRecoveryEmail(String email, List<Capsule> capsules) {
        if (capsules.isEmpty()) return;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("Your Time Capsules");

            StringBuilder html = new StringBuilder("<h1>Your Capsules</h1><ul>");
            for (Capsule c : capsules) {
                String link = baseUrl + "/capsule/" + c.getToken();
                html.append("<li><a href='").append(link).append("'>")
                    .append(c.getTitle() != null ? c.getTitle() : "Untitled Capsule")
                    .append("</a> (Unlocks: ").append(c.getUnlockAt()).append(")</li>");
            }
            html.append("</ul>");

            helper.setText(html.toString(), true);
            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Failed to send recovery email to {}: {}", email, e.getMessage());
        }
    }

    public void sendUnlockNotification(String email, Capsule capsule) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("A Memory Has Unlocked");

            String link = baseUrl + "/capsule/" + capsule.getToken();
            String html = "<h1>It's Time</h1>" +
                          "<p>A digital capsule created by " + capsule.getCreatorEmail() + " has unlocked.</p>" +
                          "<p><a href='" + link + "'>Click here to reveal the memory.</a></p>";

            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Failed to send unlock email to {}: {}", email, e.getMessage());
        }
    }
}
