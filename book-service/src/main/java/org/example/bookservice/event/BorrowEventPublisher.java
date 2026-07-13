package org.example.bookservice.event;

import lombok.RequiredArgsConstructor;
import org.example.bookservice.config.RabbitMqConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class BorrowEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(BorrowEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.BORROW_EXCHANGE,
                RabbitMqConfig.BORROW_ROUTING_KEY,
                event
        );
    }
}
