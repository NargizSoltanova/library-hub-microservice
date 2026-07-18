package org.example.bookservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.bookservice.dto.BorrowRequest;
import org.example.bookservice.dto.BorrowResponse;
import org.example.bookservice.service.BorrowService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/borrows")
@RequiredArgsConstructor
public class BorrowController {
    private final BorrowService borrowService;

    @PostMapping
    @Operation(
            summary = "Kitabın götürülməsi",
            description = "Cari istifadəçi üçün bookId ilə kitabın götürülməsi. Mövcud nüsxə olmadıqda və ya kitab " +
                    "artıq cari istifadəçidə olduqda 409 Conflict qaytarılır."
    )
    public ResponseEntity<BorrowResponse> borrow(
            @Valid @RequestBody BorrowRequest request,
            @AuthenticationPrincipal(expression = "id") Long userId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(borrowService.borrow(request.getBookId(), userId));
    }

    @PostMapping("/{id}/return")
    @Operation(
            summary = "Kitabın qaytarılması",
            description = "Cari istifadəçinin borrow ID-sinə görə götürdüyü kitabı qaytarması."
    )
    public ResponseEntity<BorrowResponse> returnBook(
            @PathVariable Long id,
            @AuthenticationPrincipal(expression = "id") Long userId) {

        return ResponseEntity.ok(borrowService.returnBook(id, userId));
    }

    @GetMapping("/my")
    @Operation(
            summary = "Cari istifadəçinin borrow siyahısı",
            description = "Cari istifadəçinin səhifələnmiş borrow siyahısının əldə olunması."
    )
    public ResponseEntity<Page<BorrowResponse>> getMyBorrows(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @ParameterObject
            @PageableDefault(sort = "borrowedAt") Pageable pageable) {
        return ResponseEntity.ok(borrowService.getMyBorrows(userId, pageable));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Bütün borrow qeydləri",
            description = "Bütün borrow qeydlərinin səhifələnmiş siyahısının əldə olunması. Yalnız ADMIN rolu üçün əlçatandır."
    )
    public ResponseEntity<Page<BorrowResponse>> getAllBorrows(@ParameterObject @PageableDefault(sort = "borrowedAt") Pageable pageable) {
        return ResponseEntity.ok(borrowService.getAllBorrows(pageable));
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Gecikmiş borrow qeydləri",
            description = "Gecikmiş borrow qeydlərinin səhifələnmiş siyahısının əldə olunması. Yalnız ADMIN rolu üçün əlçatandır."
    )
    public ResponseEntity<Page<BorrowResponse>> getOverdueBorrows(@ParameterObject @PageableDefault(sort = "dueDate") Pageable pageable) {
        return ResponseEntity.ok(borrowService.getOverdueBorrows(pageable));
    }
}
