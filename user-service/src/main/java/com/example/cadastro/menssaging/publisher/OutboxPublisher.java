package com.example.cadastro.menssaging.publisher;

import com.example.cadastro.outbox.entity.OutboxEvent;
import com.example.cadastro.outbox.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final RabbitTemplate rabbitTemplate;

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {
        List<OutboxEvent> events = outboxEventRepository.findByPublishedAtIsNull();

        for (OutboxEvent event: events) {
            publish(event);
        }
    }

    private void publish(OutboxEvent event) {

        Message message = MessageBuilder
                .withBody(event.getPayload().getBytes(StandardCharsets.UTF_8))
                .setContentType("application/json")
                .build();

        rabbitTemplate.convertAndSend(
                "user.events",
                "user.registered",
                message
        );

        event.setPublishedAt(OffsetDateTime.now());

        outboxEventRepository.save(event);
    }
}
