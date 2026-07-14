package org.project.userservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.userservice.config.RabbitMqConfig;
import org.project.userservice.event.BorrowEvent;
import org.project.userservice.service.BorrowHistoryEventService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BorrowEventConsumer {
    private final BorrowHistoryEventService eventService;

    @RabbitListener(queues = RabbitMqConfig.BORROW_QUEUE)
    public void consume(BorrowEvent event) {
        log.info(
                "Borrow event received. borrowId={}, status={}",
                event.borrowId(),
                event.status()
        );

        eventService.process(event);
    }
}
