package com.example.cadastro.menssaging.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;

@Configuration
public class RabbitMQConfig {

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

//    @Bean
//    public RabbitTemplate rabbitTemplate(
//            ConnectionFactory connectionFactory,
//            org.springframework.amqp.support.converter.JacksonJsonMessageConverter messageConverter
//    ) {
//
//        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//        rabbitTemplate.setMessageConverter(messageConverter);
//
//        return rabbitTemplate;
//    }
}
