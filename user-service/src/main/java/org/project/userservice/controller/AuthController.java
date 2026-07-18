package org.project.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.userservice.dto.auth.LoginRequest;
import org.project.userservice.dto.auth.LoginResponse;
import org.project.userservice.dto.auth.RegisterRequest;
import org.project.userservice.dto.auth.RegisterResponse;
import org.project.userservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
            summary = "İstifadəçi qeydiyyatı",
            description = "Yeni istifadəçinin qeydiyyatdan keçirilməsi. Yeni istifadəçi USER rolu ilə yaradılır."
    )
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(
            summary = "İstifadəçi girişi",
            description = "İstifadəçi adı və şifrəsi ilə giriş edilərək JWT token əldə olunması."
    )
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }
}
