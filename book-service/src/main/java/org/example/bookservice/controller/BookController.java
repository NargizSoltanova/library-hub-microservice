package org.example.bookservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.bookservice.dto.BookRequest;
import org.example.bookservice.dto.BookResponse;
import org.example.bookservice.service.BookService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping
    @Operation(
            summary = "Kitab siyahısı",
            description = "Kateqoriya və müəllif filtrləri, səhifələmə və sıralama ilə kitab siyahısının əldə olunması."
    )
    public ResponseEntity<Page<BookResponse>> getBooks(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String author,
            @ParameterObject
            @PageableDefault(sort = "title") Pageable pageable) {
        return ResponseEntity.ok(
                bookService.getBooks(categoryId, author, pageable)
        );
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Kitab detalı",
            description = "ID-yə görə kitab məlumatlarının əldə olunması."
    )
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @GetMapping("/search")
    @Operation(
            summary = "Kitab axtarışı",
            description = "Açar sözə əsasən kitab adı və müəllif üzrə səhifələnmiş axtarışın aparılması."
    )
    public ResponseEntity<Page<BookResponse>> search(
            @RequestParam String keyword,
            @ParameterObject
            @PageableDefault(sort = "title") Pageable pageable) {
        return ResponseEntity.ok(
                bookService.search(keyword, pageable)
        );
    }

    @PostMapping
    @Operation(
            summary = "Kitab yaradılması",
            description = "Yeni kitabın yaradılması. Yalnız ADMIN rolu üçün əlçatandır."
    )
    public ResponseEntity<BookResponse> create(@Valid @RequestBody BookRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bookService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Kitabın yenilənməsi",
            description = "ID-yə görə kitabın yenilənməsi. Yalnız ADMIN rolu üçün əlçatandır."
    )
    public ResponseEntity<BookResponse> update(@PathVariable Long id, @Valid @RequestBody BookRequest request) {
        return ResponseEntity.ok(bookService.update(id, request));
    }

    @PatchMapping("/{id}/activate")
    @Operation(
            summary = "Kitabın aktivləşdirilməsi",
            description = "ID-yə görə deaktiv kitabın yenidən aktivləşdirilməsi. Yalnız ADMIN rolu üçün əlçatandır."
    )
    public ResponseEntity<BookResponse> activate(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.activate(id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Kitabın silinməsi",
            description = "ID-yə görə kitabın soft delete edilməsi. Yalnız ADMIN rolu üçün əlçatandır."
    )
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
