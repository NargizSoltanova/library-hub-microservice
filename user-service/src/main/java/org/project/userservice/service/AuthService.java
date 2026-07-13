package org.project.userservice.service;

import lombok.AllArgsConstructor;
import org.project.userservice.config.JwtProperties;
import org.project.userservice.constant.Role;
import org.project.userservice.dto.auth.LoginRequest;
import org.project.userservice.dto.auth.LoginResponse;
import org.project.userservice.dto.auth.RegisterRequest;
import org.project.userservice.dto.auth.RegisterResponse;
import org.project.userservice.entity.UserEntity;
import org.project.userservice.exception.BadRequestException;
import org.project.userservice.exception.ConflictException;
import org.project.userservice.repository.UserRepository;
import org.project.userservice.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class AuthService {
    private final JwtProperties  jwtProperties;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword())
        );

        UserEntity user = (UserEntity) authentication.getPrincipal();

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtService.generateToken(user);

        return LoginResponse.builder()
                .accessToken(token)
                .expiresIn(jwtProperties.getExpiration())
                .build();
    }

    public RegisterResponse register(RegisterRequest registerRequest) {

        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new ConflictException(
                    "Username already exists."
            );
        }

        if(userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ConflictException(
                    "Email already exists."
            );
        }

        var user = UserEntity.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .fullName(registerRequest.getFullName())
                .role(Role.USER)
                .isActive(true)
                .build();

        var savedUser = userRepository.save(user);

        return RegisterResponse.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .createdAt(savedUser.getCreatedAt())
                .build();
    }
}
