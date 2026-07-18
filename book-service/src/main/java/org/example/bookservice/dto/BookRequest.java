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
    @NotBlank(message = "Book title must not be blank")
    private String title;

    @NotBlank(message = "Author name must not be blank")
    @Size(max = 150, message = "Author name must not exceed 150 characters")
    private String author;

    @NotBlank(message = "ISBN must not be blank")
    @Size(max = 20, message = "ISBN must not exceed 20 characters")
    private String isbn;

    private String description;

    @NotNull(message = "Category ID is required")
    @Positive(message = "Category ID must be positive")
    private Long categoryId;

    @NotNull(message = "Total copies is required")
    @PositiveOrZero(message = "Total copies must not be negative")
    private Integer totalCopies;

    @NotNull(message = "Available copies is required")
    @PositiveOrZero(message = "Available copies must not be negative")
    private Integer availableCopies;

    @Min(value = 1, message = "Published year must be greater than 0")
    private Integer publishedYear;
}
