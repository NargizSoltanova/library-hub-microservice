package org.example.bookservice.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bookservice.constant.BorrowStatus;
import org.example.bookservice.repository.BorrowRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class BorrowScheduler {

    private final BorrowRepository borrowRepository;

    @Transactional
    @Scheduled(
            cron = "0 0 0 * * *",
            zone = "Asia/Baku"
    )
    public void markOverdueBorrows() {
        var today = LocalDate.now();

        int updatedCount =
                borrowRepository.markExpiredBorrowsAsOverdue(
                        today,
                        BorrowStatus.BORROWED,
                        BorrowStatus.OVERDUE
                );

        log.info(
                "Overdue borrow update completed: {}",
                updatedCount
        );
    }
}
