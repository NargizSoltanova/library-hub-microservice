package org.project.userservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookResponse {
    private Long bookId;
    private String bookName;
}