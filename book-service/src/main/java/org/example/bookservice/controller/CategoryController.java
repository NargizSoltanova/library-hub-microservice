package org.example.bookservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.bookservice.dto.CategoryRequest;
import org.example.bookservice.dto.CategoryResponse;
import org.example.bookservice.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    @Operation(
            summary = "Kateqoriya siyahısı",
            description = "Bütün kateqoriyaların siyahısının əldə olunması."
    )
    public List<CategoryResponse> getAll() {
        return categoryService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Kateqoriya detalı",
            description = "ID-yə görə kateqoriya məlumatlarının əldə olunması."
    )
    public CategoryResponse getById(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Kateqoriya yaradılması",
            description = "Yeni kateqoriyanın yaradılması. Yalnız ADMIN rolu üçün əlçatandır."
    )
    public CategoryResponse create(@Valid @RequestBody CategoryRequest request) {
        return categoryService.create(request);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Kateqoriyanın yenilənməsi",
            description = "ID-yə görə kateqoriyanın yenilənməsi. Yalnız ADMIN rolu üçün əlçatandır."
    )
    public CategoryResponse update(@PathVariable Long id,
                                   @Valid @RequestBody CategoryRequest request) {
        return categoryService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Kateqoriyanın silinməsi",
            description = "ID-yə görə kateqoriyanın silinməsi. Kitab tərəfindən istifadə edilən kateqoriya silinə " +
                    "bilməz və 409 Conflict qaytarılır. Yalnız ADMIN rolu üçün əlçatandır."
    )
    public void delete(@PathVariable Long id) {
        categoryService.delete(id);
    }
}
