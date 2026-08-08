package com.dev.notification_service.menssaging.consumer;

import com.dev.notification_service.menssaging.events.UserRegisteredEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class UserRegisteredEventConsumer {

    @RabbitListener(queues = "notification.user.registered")
    public void consume(UserRegisteredEvent event) {
        System.out.println("Evento recebido: " +event);
    }
}
