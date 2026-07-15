package org.project.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.userservice.config.RabbitMqConfig;
import org.project.userservice.constant.Status;
import org.project.userservice.event.BorrowEvent;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeadLetterReprocessService {
    private static final String REPUBLISH_COUNT_HEADER = "x-republish-count";
    private static final int MAX_REPUBLISH_COUNT = 3;

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public boolean reprocessOneMessage() {
        var deadMessage = rabbitTemplate.receive(RabbitMqConfig.BORROW_DEAD_QUEUE);

        if (deadMessage == null)
            return false;

        try {
            var event = convertToBorrowEvent(deadMessage);

            int republishCount = getRepublishCount(deadMessage);

            if (republishCount >= MAX_REPUBLISH_COUNT) {
                log.error(
                        "Borrow event reached maximum DLQ reprocess count. borrowId={}, count={}",
                        event.borrowId(),
                        republishCount
                );

                returnToDeadQueue(event, republishCount);
                return true;
            }

            String routingKey = getRoutingKey(event.status());

            int newRepublishCount = republishCount + 1;

            var republishedMessage = createMessage(event, newRepublishCount);

            rabbitTemplate.send(
                    RabbitMqConfig.BORROW_EXCHANGE,
                    routingKey,
                    republishedMessage
            );

            log.info(
                    "Borrow event republished from DLQ. borrowId={}, status={}, routingKey={}, count={}",
                    event.borrowId(),
                    event.status(),
                    routingKey,
                    newRepublishCount
            );

            return true;
        } catch (Exception exception) {
            log.error("DLQ event could not be republished", exception);

            rabbitTemplate.send(
                    RabbitMqConfig.BORROW_DEAD_EXCHANGE,
                    RabbitMqConfig.BORROW_DEAD_KEY,
                    deadMessage
            );

            throw exception;
        }
    }

    private BorrowEvent convertToBorrowEvent(Message message) {
        try {
            return objectMapper.readValue(
                    message.getBody(),
                    BorrowEvent.class
            );
        } catch (Exception exception) {
            throw new IllegalArgumentException(
                    "DLQ message cannot be converted to BorrowEvent", exception);
        }
    }

    private int getRepublishCount(Message message) {
        Object header = message
                .getMessageProperties()
                .getHeader(REPUBLISH_COUNT_HEADER);

        if (header instanceof Number number) {
            return number.intValue();
        }

        return 0;
    }

    private String getRoutingKey(Status status) {
        return switch (status) {
            case BORROWED -> RabbitMqConfig.BORROW_CREATED_KEY;

            case RETURNED -> RabbitMqConfig.BORROW_RETURNED_KEY;

            default ->
                    throw new IllegalArgumentException("Routing key is not configured");
        };
    }

    private Message createMessage(BorrowEvent event, int republishCount) {
        var properties = new MessageProperties();

        properties.setHeader(REPUBLISH_COUNT_HEADER, republishCount);

        return rabbitTemplate
                .getMessageConverter()
                .toMessage(event, properties);
    }

    private void returnToDeadQueue(BorrowEvent event, int republishCount) {
        var message = createMessage(event, republishCount);

        rabbitTemplate.send(
                RabbitMqConfig.BORROW_DEAD_EXCHANGE,
                RabbitMqConfig.BORROW_DEAD_KEY,
                message
        );
    }
}