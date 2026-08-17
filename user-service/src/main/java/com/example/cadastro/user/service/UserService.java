package com.example.cadastro.user.service;

import com.example.cadastro.exception.EmailAlreadyExistsException;
import com.example.cadastro.menssaging.events.UserRegisteredEvent;
import com.example.cadastro.menssaging.publisher.UserEventPublisher;
import com.example.cadastro.outbox.entity.OutboxEvent;
import com.example.cadastro.outbox.factory.OutboxEventFactory;
import com.example.cadastro.outbox.repository.OutboxEventRepository;
import com.example.cadastro.user.dto.CreateUserRequest;
import com.example.cadastro.user.dto.UserResponse;
import com.example.cadastro.user.entitiy.User;
import com.example.cadastro.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final OutboxEventRepository outboxEventRepository;

    private final OutboxEventFactory outboxEventFactory;

    @Transactional
    public UserResponse register(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        User user = User.create(request.name(), request.email());

        User savedUser = userRepository.save(user);

        UserRegisteredEvent event = UserRegisteredEvent.create(user.getId(), user.getEmail());

        OutboxEvent outboxEvent = outboxEventFactory.create(event);

        outboxEventRepository.save(outboxEvent);

        return new UserResponse(savedUser.getId(), savedUser.getName(), savedUser.getEmail(), savedUser.getCreatedAt());
    }
}
