package org.project.userservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    public static final String BORROW_EXCHANGE = "borrow.exchange";
    public static final String BORROW_QUEUE = "borrow.queue";
    public static final String BORROW_CREATED_KEY = "borrow.created";
    public static final String BORROW_RETURNED_KEY = "borrow.returned";


    @Bean
    public TopicExchange borrowExchange() {
        return new TopicExchange(BORROW_EXCHANGE, true, false);
    }

    @Bean
    public Queue borrowQueue() {
        return QueueBuilder.durable(BORROW_QUEUE).build();
    }

    @Bean
    public Binding borrowCreatedBinding(Queue borrowQueue, TopicExchange borrowExchange) {
        return BindingBuilder
                .bind(borrowQueue)
                .to(borrowExchange)
                .with(BORROW_CREATED_KEY);
    }

    @Bean
    public Binding borrowReturnedBinding(Queue borrowQueue, TopicExchange borrowExchange) {
        return BindingBuilder
                .bind(borrowQueue)
                .to(borrowExchange)
                .with(BORROW_RETURNED_KEY);
    }

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
