package org.project.userservice.dto;

import lombok.*;
import org.project.userservice.constant.Status;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BorrowHistoryResponse {
    private Long id;
    private UserResponse user;
    private BookResponse book;
    private LocalDateTime borrowedAt;
    private LocalDateTime returnedAt;
    private Status status;
}