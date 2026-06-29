package org.project.userservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.project.userservice.dto.BookResponse;
import org.project.userservice.dto.BorrowHistoryResponse;
import org.project.userservice.dto.UserBorrowHistoryDto;
import org.project.userservice.entity.BorrowHistoryEntity;

@Mapper(componentModel = "spring",
        uses = UserMapper.class)
public interface BorrowHistoryMapper {
    @Mapping(target = "book", source = ".")
    BorrowHistoryResponse toResponse(BorrowHistoryEntity borrowHistory);

    @Mapping(target = "book", source = ".")
    UserBorrowHistoryDto toDto(BorrowHistoryEntity borrowHistory);

    @Mapping(target = "bookId", source = "bookId")
    @Mapping(target = "bookName", source = "bookName")
    BookResponse toBookResponse(BorrowHistoryEntity borrowHistory);
}