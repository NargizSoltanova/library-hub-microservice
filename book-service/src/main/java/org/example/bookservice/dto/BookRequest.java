package org.example.bookservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookRequest {
    @NotBlank()
    private String title;

    @NotBlank()
    @Size(max = 150)
    private String author;

    @NotBlank()
    @Size(max = 20)
    private String isbn;

    private String description;

    @NotNull()
    @Positive()
    private Long categoryId;

    @NotNull()
    @PositiveOrZero()
    private Integer totalCopies;

    @NotNull()
    @PositiveOrZero()
    private Integer availableCopies;

    @Min(value = 0)
    private Integer publishedYear;
}
