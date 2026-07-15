package org.example.bookservice.dto;

import lombok.*;
import org.example.bookservice.constant.BorrowStatus;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BorrowResponse implements Serializable {
    private Long id;
    private Long userId;
    private BorrowBookResponse book;
    private LocalDateTime borrowedAt;
    private LocalDate dueDate;
    private LocalDateTime returnedAt;
    private BorrowStatus status;
}
