package org.example.bookservice.event;

import org.example.bookservice.constant.BorrowStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

public record BorrowEvent(
        Long borrowId,
        Long userId,
        Long bookId,
        String bookName,
        LocalDateTime borrowedAt,
        LocalDateTime returnedAt,
        BorrowStatus status
) implements Serializable {
}
