package com.gsrm.maas.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${spring.mail.username:noreply@gsrm.local}")
    private String from;

    /** Envoi asynchrone : un échec SMTP ne bloque jamais le traitement de la demande. */
    @Async
    public void send(String to, String subject, String body) {
        if (!mailEnabled || to == null || to.isBlank()) {
            log.info("[EMAIL désactivé] to={} subject={}", to, subject);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Echec d'envoi d'email à {} : {}", to, e.getMessage());
        }
    }
}
