package org.example.bookservice.mapper;

import org.example.bookservice.dto.BorrowBookResponse;
import org.example.bookservice.dto.BorrowResponse;
import org.example.bookservice.entity.BookEntity;
import org.example.bookservice.entity.BorrowEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BorrowMapper {
    BorrowResponse toResponse(BorrowEntity borrow);

    BorrowBookResponse toResponse(BookEntity book);
}
