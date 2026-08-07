package com.example.cadastro.menssaging.publisher;

import com.example.cadastro.menssaging.events.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(UserRegisteredEvent event) {
        rabbitTemplate.convertAndSend(
                "user.events",
                "user.registered",
                event
        );
    }
}
