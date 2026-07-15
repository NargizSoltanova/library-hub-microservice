package org.example.bookservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BorrowRequest {
    @NotNull()
    @Positive()
    private Long bookId;
}
