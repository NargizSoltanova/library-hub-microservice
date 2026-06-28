package org.project.userservice.service;

import lombok.AllArgsConstructor;
import org.project.userservice.config.JwtProperties;
import org.project.userservice.constant.Role;
import org.project.userservice.dto.auth.LoginRequest;
import org.project.userservice.dto.auth.LoginResponse;
import org.project.userservice.dto.auth.RegisterRequest;
import org.project.userservice.dto.auth.RegisterResponse;
import org.project.userservice.entity.UserEntity;
import org.project.userservice.repository.UserRepository;
import org.project.userservice.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final JwtProperties  jwtProperties;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest loginRequest) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword())
        );

        UserEntity user = (UserEntity) authentication.getPrincipal();

        String token = jwtService.generateToken(user);

        return LoginResponse.builder()
                .accessToken(token)
                .expiresIn(jwtProperties.getExpiration())
                .build();
    }

    public RegisterResponse register(RegisterRequest registerRequest) {
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
