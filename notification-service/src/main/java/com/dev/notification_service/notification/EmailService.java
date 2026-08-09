package com.dev.notification_service.notification;

import com.dev.notification_service.menssaging.events.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendWelcomeEmail(UserRegisteredEvent event) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(event.email());
        message.setSubject("Bem-vindo!");
        message.setText(
                "Olá!\n\n"
                        + "Seu cadastro foi realizado com sucesso."
        );

        mailSender.send(message);
    }
}
