package org.project.userservice.mapper;

import org.mapstruct.Mapper;
import org.project.userservice.dto.UserResponse;
import org.project.userservice.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(UserEntity user);
}
