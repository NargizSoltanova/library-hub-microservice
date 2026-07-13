package org.example.bookservice.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowBookResponse implements Serializable {
    private Long id;
    private String title;
    private String author;
    private String isbn;
}
