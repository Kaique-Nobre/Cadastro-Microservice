package com.example.cadastro.menssaging.events;

import java.time.Instant;
import java.util.UUID;

public record UserRegisteredEvent(
        UUID eventId,
        UUID userId,
        String email,
        Instant occurredAt
) {

    public static UserRegisteredEvent create(UUID userId, String email) {
        return new UserRegisteredEvent(
                UUID.randomUUID(),
                userId,
                email,
                Instant.now()
        );
    }
}
