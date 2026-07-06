package org.example.bookservice.mapper;

import org.example.bookservice.dto.CategoryRequest;
import org.example.bookservice.dto.CategoryResponse;
import org.example.bookservice.entity.CategoryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryEntity toEntity(CategoryRequest request);
    CategoryResponse toResponse(CategoryEntity entity);
}
