package com.dev.notification_service.menssaging.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.StatelessRetryOperationsInterceptor;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public StatelessRetryOperationsInterceptor retryInterceptor() {
        return RetryInterceptorBuilder
                .stateless()
                .maxRetries(2)
                .backOffOptions(1000, 1.0, 1000)
                .recoverer(new RejectAndDontRequeueRecoverer())
                .build();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter messageConverter,
            StatelessRetryOperationsInterceptor retryInterceptor) {

        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setAdviceChain(retryInterceptor);

        return factory;
    }

    @Bean
    public DirectExchange userEventsExchange() {
        return new DirectExchange("user.events");
    }

    @Bean
    public Queue userRegisteredQueue() {
        return QueueBuilder
                .durable("notification.user.registered")
                .deadLetterExchange("notification.dlx")
                .deadLetterRoutingKey("user.registered")
                .build();
    }

    @Bean
    public Binding userRegisteredBinding(Queue userRegisteredQueue, DirectExchange userEventsExchange) {
        return BindingBuilder
                .bind(userRegisteredQueue)
                .to(userEventsExchange)
                .with("user.registered");
    }

    @Bean
    public DirectExchange notificationDlx() {
        return new DirectExchange("notification.dlx");
    }

    @Bean
    public Queue userRegisteredDlq() {
        return new Queue("notification.user.dlq");
    }

    @Bean
    public Binding userRegisteredDlqBinding(Queue userRegisteredDlq, DirectExchange notificationDlx) {
        return BindingBuilder
                .bind(userRegisteredDlq)
                .to(notificationDlx)
                .with("user.registered");
    }
}
