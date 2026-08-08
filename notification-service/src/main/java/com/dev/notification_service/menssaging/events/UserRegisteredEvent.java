package com.dev.notification_service.menssaging.events;

import java.time.Instant;
import java.util.UUID;

public record UserRegisteredEvent(
        UUID eventId,
        UUID userId,
        String email,
        Instant occurredAt
) {
}
