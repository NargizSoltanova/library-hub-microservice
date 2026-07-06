package org.example.bookservice.service;

import lombok.RequiredArgsConstructor;
import org.example.bookservice.dto.CategoryRequest;
import org.example.bookservice.dto.CategoryResponse;
import org.example.bookservice.entity.CategoryEntity;
import org.example.bookservice.exception.CategoryNotFoundException;
import org.example.bookservice.exception.ConflictException;
import org.example.bookservice.mapper.CategoryMapper;
import org.example.bookservice.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    public CategoryResponse getById(Long id) {
        CategoryEntity category = findById(id);
        return categoryMapper.toResponse(category);
    }

    public CategoryResponse create(CategoryRequest request) {
        request.setName(request.getName().trim());

        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new ConflictException("Category with name " + request.getName() + " already exists");
        }

        var category = categoryMapper.toEntity(request);
        var savedCategory = categoryRepository.save(category);

        return categoryMapper.toResponse(savedCategory);
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        var category = findById(id);
        request.setName(request.getName().trim());

        if (!category.getName().equals(request.getName())
                && categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new ConflictException("Category with name " + request.getName() + " already exists");
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        var updatedCategory = categoryRepository.save(category);

        return categoryMapper.toResponse(updatedCategory);
    }

    public void delete(Long id) {
        CategoryEntity category = findById(id);
        categoryRepository.delete(category);
    }

    private CategoryEntity findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
    }
}
