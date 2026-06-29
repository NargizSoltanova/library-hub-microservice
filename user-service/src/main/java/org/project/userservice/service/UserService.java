package org.project.userservice.service;

import lombok.AllArgsConstructor;
import org.project.userservice.dto.UserResponse;
import org.project.userservice.dto.UserUpdateDto;
import org.project.userservice.entity.UserEntity;
import org.project.userservice.exception.UserNotFoundException;
import org.project.userservice.mapper.UserMapper;
import org.project.userservice.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponse me(){
        UserEntity user = getUserEntity();
        return userMapper.toResponse(user);
    }

    public UserResponse update(UserUpdateDto userDto){
        UserEntity user = getUserEntity();
        user.setEmail(userDto.getEmail());
        user.setFullName(userDto.getFullName());
        var updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    public void deleteUser(Long id){
        UserEntity user = getUserEntity(id);
        user.setIsActive(false);
        userRepository.save(user);
    }

    public UserResponse getUserById(Long id){
        UserEntity user = getUserEntity(id);
        return userMapper.toResponse(user);
    }

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toResponse);
    }

    public UserEntity getUserEntity(Long id) {
        return userRepository.findById(id).orElseThrow(
                () ->  new UserNotFoundException("User not found"));
    }

    public UserEntity getUserEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UserNotFoundException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserEntity user)) {
            throw new UserNotFoundException("User not found");
        }
        return user;
    }
}
