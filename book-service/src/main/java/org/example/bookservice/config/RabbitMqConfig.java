package org.example.bookservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    public static final String BORROW_EXCHANGE = "borrow.exchange";
    public static final String BORROW_QUEUE = "borrow.queue";
    public static final String BORROW_ROUTING_KEY = "borrow.key";

    @Bean
    public DirectExchange borrowExchange() {
        return new DirectExchange(BORROW_EXCHANGE, true, false);
    }

    @Bean
    public Queue borrowQueue() {
        return QueueBuilder.durable(BORROW_QUEUE).build();
    }

    @Bean
    public Binding borrowBinding(Queue borrowQueue, DirectExchange borrowExchange) {
        return BindingBuilder
                .bind(borrowQueue)
                .to(borrowExchange)
                .with(BORROW_ROUTING_KEY);
    }

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
