package org.project.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.userservice.dto.UserBorrowHistoryDto;
import org.project.userservice.dto.UserResponse;
import org.project.userservice.dto.UserUpdateDto;
import org.project.userservice.service.BorrowHistoryService;
import org.project.userservice.service.UserService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private  final BorrowHistoryService borrowHistoryService;

    @GetMapping("/me")
    @Operation(
            summary = "Cari istifadəçi profili",
            description = "Cari istifadəçinin profil məlumatlarının əldə olunması."
    )
    public ResponseEntity<UserResponse> me() {
        return ResponseEntity.ok(userService.me());
    }

    @PutMapping("/me")
    @Operation(
            summary = "Cari istifadəçi profilinin yenilənməsi",
            description = "Cari istifadəçinin profil məlumatlarının yenilənməsi."
    )
    public ResponseEntity<UserResponse> updateMe(
            @Valid @RequestBody UserUpdateDto userDto) {

        return ResponseEntity.ok(userService.update(userDto));
    }

    @GetMapping("/me/borrows")
    @Operation(
            summary = "Cari istifadəçinin borrow tarixçəsi",
            description = "Cari istifadəçinin borrow tarixçəsinin əldə olunması."
    )
    public ResponseEntity<List<UserBorrowHistoryDto>>  meBorrowHistory(){
        return ResponseEntity.ok(borrowHistoryService.myBorrowHistory());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "İstifadəçi siyahısı",
            description = "İstifadəçilərin səhifələnmiş siyahısının əldə olunması. Yalnız ADMIN rolu üçün əlçatandır."
    )
    public Page<UserResponse> getAllUsers(@ParameterObject Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "İstifadəçi detalı",
            description = "ID-yə görə istifadəçi məlumatlarının əldə olunması. Yalnız ADMIN rolu üçün əlçatandır."
    )
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {

        return ResponseEntity.ok(userService.getUserById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "İstifadəçinin silinməsi",
            description = "ID-yə görə istifadəçinin soft delete edilməsi. Yalnız ADMIN rolu üçün əlçatandır."
    )
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id) {

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
