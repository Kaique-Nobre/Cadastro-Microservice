package com.example.cadastro.outbox.factory;

import com.example.cadastro.menssaging.events.UserRegisteredEvent;
import com.example.cadastro.outbox.entity.OutboxEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class OutboxEventFactory {

    private final ObjectMapper objectMapper;

    public OutboxEvent create(UserRegisteredEvent event) {
        try {
            OutboxEvent outboxEvent = new OutboxEvent();

            outboxEvent.setEventType("UserRegisteredEvent");
            outboxEvent.setAggregateType("User");
            outboxEvent.setAggregateId(event.userId());
            outboxEvent.setPayload(objectMapper.writeValueAsString(event));
            outboxEvent.setCreatedAt(OffsetDateTime.now());

            return outboxEvent;

        } catch (JacksonException exception) {
            throw new IllegalStateException(
                    "Could not serialize outbox event",
                    exception
            );
        }
    }
}
