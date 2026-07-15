package org.example.bookservice.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String description;
    private CategoryResponse category;
    private Integer totalCopies;
    private Integer availableCopies;
    private Integer publishedYear;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
