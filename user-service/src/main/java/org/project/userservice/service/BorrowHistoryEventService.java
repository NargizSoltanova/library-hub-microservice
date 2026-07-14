package org.project.userservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.userservice.constant.Status;
import org.project.userservice.entity.BorrowHistoryEntity;
import org.project.userservice.entity.UserEntity;
import org.project.userservice.event.BorrowEvent;
import org.project.userservice.exception.BorrowHistoryNotFoundException;
import org.project.userservice.exception.UserNotFoundException;
import org.project.userservice.repository.BorrowHistoryRepository;
import org.project.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class BorrowHistoryEventService {
    private final UserRepository userRepository;
    private final BorrowHistoryRepository borrowHistoryRepository;

    @Transactional
    public void process(BorrowEvent event) {
        if (event == null || event.status() == null)
            throw new IllegalArgumentException("Borrow event and status cannot be null");

        switch (event.status()) {
            case BORROWED -> createBorrowHistory(event);
            case RETURNED -> returnBorrow(event);
        }
    }

    private void createBorrowHistory(BorrowEvent event) {
        if (borrowHistoryRepository.existsByBorrowId(event.borrowId())) {
            log.info("Borrow event already processed. borrowId={}", event.borrowId());
            return;
        }

        var user = userRepository.findById(event.userId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + event.userId()));

        var history = buildBorrowHistory(event, user);

        borrowHistoryRepository.save(history);

        log.info("Borrow history created. borrowId={}", event.borrowId());
    }

    private void returnBorrow(BorrowEvent event) {
        var history = findBorrowHistory(event.borrowId());

        if (history.getStatus() == Status.RETURNED) {
            log.info("Return event already processed. borrowId={}", event.borrowId());
            return;
        }

        history.setStatus(Status.RETURNED);
        history.setReturnedAt(event.returnedAt());

        borrowHistoryRepository.save(history);

        log.info("Borrow history marked as returned. borrowId={}", event.borrowId());
    }

    private static BorrowHistoryEntity buildBorrowHistory(BorrowEvent event, UserEntity user) {
        return BorrowHistoryEntity.builder()
                .borrowId(event.borrowId())
                .user(user)
                .bookId(event.bookId())
                .bookName(event.bookName())
                .borrowedAt(event.borrowedAt())
                .returnedAt(null)
                .status(Status.BORROWED)
                .build();
    }

    private BorrowHistoryEntity findBorrowHistory(Long borrowId) {
        return borrowHistoryRepository.findByBorrowId(borrowId)
                .orElseThrow(() -> new BorrowHistoryNotFoundException("Borrow history not found with borrowId: " + borrowId));
    }
}
