package org.example.bookservice.event;

import lombok.RequiredArgsConstructor;
import org.example.bookservice.config.RabbitMqConfig;
import org.example.bookservice.constant.BorrowStatus;
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
        var routingKey = getRoutingKey(event.status());

        rabbitTemplate.convertAndSend(
                RabbitMqConfig.BORROW_EXCHANGE,
                routingKey,
                event
        );
    }

    private String getRoutingKey(BorrowStatus status) {
        return switch (status) {
            case BORROWED -> RabbitMqConfig.BORROW_CREATED_KEY;

            case RETURNED -> RabbitMqConfig.BORROW_RETURNED_KEY;

            default -> throw new IllegalArgumentException("Routing key is not configured");
        };
    }
}
