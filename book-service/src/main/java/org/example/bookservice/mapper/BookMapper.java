package org.example.bookservice.mapper;

import org.example.bookservice.dto.BookResponse;
import org.example.bookservice.entity.BookEntity;
import org.mapstruct.Mapper;


@Mapper(
        componentModel = "spring",
        uses = CategoryMapper.class)
public interface BookMapper {
    BookResponse toResponse(BookEntity book);
}
