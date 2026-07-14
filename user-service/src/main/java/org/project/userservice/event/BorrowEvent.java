package org.project.userservice.event;

import org.project.userservice.constant.Status;

import java.io.Serializable;
import java.time.LocalDateTime;

public record BorrowEvent(
        Long borrowId,
        Long userId,
        Long bookId,
        String bookName,
        LocalDateTime borrowedAt,
        LocalDateTime returnedAt,
        Status status
) implements Serializable {
}
