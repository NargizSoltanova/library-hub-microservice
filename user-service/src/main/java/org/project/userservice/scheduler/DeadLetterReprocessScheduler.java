package org.project.userservice.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.userservice.service.DeadLetterReprocessService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeadLetterReprocessScheduler {
    private static final int MAX_MESSAGES_PER_RUN = 100;

    private final DeadLetterReprocessService reprocessService;

    @Scheduled(
            fixedDelay = 300_000, //5m
            initialDelay = 10_000
    )
    public void reprocessDeadLetters() {
        log.info("DLQ reprocessing job started");

        int processedCount = 0;

        while (processedCount < MAX_MESSAGES_PER_RUN) {
            try {
                boolean messageFound = reprocessService.reprocessOneMessage();

                if (!messageFound)
                    break;
            }catch (Exception exception) {
                log.error(
                        "DLQ reprocessing failed after {} messages",
                        processedCount,
                        exception
                );

                break;
            }


            processedCount++;
        }

        log.info("DLQ reprocessing job completed. Republished messages: {}", processedCount);
    }
}
