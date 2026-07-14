package org.example.bookservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.bookservice.dto.BorrowRequest;
import org.example.bookservice.dto.BorrowResponse;
import org.example.bookservice.service.BorrowService;
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
    public ResponseEntity<BorrowResponse> borrow(
            @Valid @RequestBody BorrowRequest request,
            @AuthenticationPrincipal(expression = "id") Long userId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(borrowService.borrow(request.getBookId(), userId));
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<BorrowResponse> returnBook(
            @PathVariable Long id,
            @AuthenticationPrincipal(expression = "id") Long userId) {

        return ResponseEntity.ok(borrowService.returnBook(id, userId));
    }

    @GetMapping("/my")
    public ResponseEntity<Page<BorrowResponse>> getMyBorrows(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @PageableDefault(sort = "borrowedAt") Pageable pageable) {
        return ResponseEntity.ok(borrowService.getMyBorrows(userId, pageable));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BorrowResponse>> getAllBorrows(@PageableDefault(sort = "borrowedAt") Pageable pageable) {
        return ResponseEntity.ok(borrowService.getAllBorrows(pageable));
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BorrowResponse>> getOverdueBorrows(@PageableDefault(sort = "dueDate") Pageable pageable) {
        return ResponseEntity.ok(borrowService.getOverdueBorrows(pageable));
    }
}
