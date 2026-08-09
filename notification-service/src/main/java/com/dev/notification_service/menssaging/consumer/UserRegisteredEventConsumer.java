package com.dev.notification_service.menssaging.consumer;

import com.dev.notification_service.menssaging.events.UserRegisteredEvent;
import com.dev.notification_service.notification.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRegisteredEventConsumer {

    private final EmailService emailService;

    @RabbitListener(queues = "notification.user.registered")
    public void consume(UserRegisteredEvent event) {
        System.out.println("Evento recebido: " +event);

        emailService.sendWelcomeEmail(event);

    }
}
