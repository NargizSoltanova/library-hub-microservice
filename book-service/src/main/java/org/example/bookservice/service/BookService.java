package org.example.bookservice.service;

import lombok.RequiredArgsConstructor;
import org.example.bookservice.dto.BookRequest;
import org.example.bookservice.dto.BookResponse;
import org.example.bookservice.entity.BookEntity;
import org.example.bookservice.entity.CategoryEntity;
import org.example.bookservice.exception.BadRequestException;
import org.example.bookservice.exception.BookNotFoundException;
import org.example.bookservice.exception.CategoryNotFoundException;
import org.example.bookservice.exception.ConflictException;
import org.example.bookservice.mapper.BookMapper;
import org.example.bookservice.repository.BookRepository;
import org.example.bookservice.repository.CategoryRepository;
import org.example.bookservice.specification.BookSpecification;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapper;

    @Cacheable(
            value = "books",
            key = "'list:' + #pageable.pageNumber"
                    + " + ':' + #pageable.pageSize"
                    + " + ':' + #pageable.sort"
                    + " + ':' + #categoryId"
                    + " + ':' + (#author == null ? '' : #author.trim().toLowerCase())"
    )
    @Transactional(readOnly = true)
    public Page<BookResponse> getBooks(Long categoryId, String author, Pageable pageable) {
        return bookRepository.findAll(BookSpecification.filter(categoryId, author), pageable)
                .map(bookMapper::toResponse);
    }

    @Cacheable(value = "books", key = "'detail:' + #id")
    @Transactional(readOnly = true)
    public BookResponse getBookById(Long id) {
        var book = getActiveBookEntity(id);
        return bookMapper.toResponse(book);
    }

    @Cacheable(
            value = "books",
            key = "'search:' + #keyword.trim().toLowerCase()"
                    + " + ':' + #pageable.pageNumber"
                    + " + ':' + #pageable.pageSize"
                    + " + ':' + #pageable.sort"
    )
    @Transactional(readOnly = true)
    public Page<BookResponse> search(String keyword, Pageable pageable) {
        return bookRepository.findAll(BookSpecification.search(keyword), pageable)
                .map(bookMapper::toResponse);
    }

    @Transactional
    @CacheEvict(value = "books", allEntries = true)
    public BookResponse create(BookRequest request) {
        validateCopyCounts(request);
        validateUniqueIsbn(request.getIsbn(), null);

        var category = getCategory(request.getCategoryId());

        var book = BookEntity.builder()
                .title(request.getTitle().trim())
                .author(request.getAuthor().trim())
                .isbn(normalizeIsbn(request.getIsbn()))
                .description(request.getDescription())
                .category(category)
                .totalCopies(request.getTotalCopies())
                .availableCopies(request.getAvailableCopies())
                .publishedYear(request.getPublishedYear())
                .isActive(true)
                .build();

        return bookMapper.toResponse(bookRepository.save(book));
    }

    @Transactional
    @CacheEvict(value = "books", allEntries = true)
    public BookResponse update(Long id, BookRequest request) {
        validateCopyCounts(request);
        validateUniqueIsbn(request.getIsbn(), id);

        var book = getActiveBookEntity(id);
        var category = getCategory(request.getCategoryId());

        book.setTitle(request.getTitle().trim());
        book.setAuthor(request.getAuthor().trim());
        book.setIsbn(normalizeIsbn(request.getIsbn()));
        book.setDescription(request.getDescription());
        book.setCategory(category);
        book.setTotalCopies(request.getTotalCopies());
        book.setAvailableCopies(request.getAvailableCopies());
        book.setPublishedYear(request.getPublishedYear());

        return bookMapper.toResponse(bookRepository.save(book));
    }

    @Transactional
    @CacheEvict(value = "books", allEntries = true)
    public void delete(Long id) {
        BookEntity book = getActiveBookEntity(id);
        book.setIsActive(false);
        bookRepository.save(book);
    }

    private BookEntity getActiveBookEntity(Long id) {
        return bookRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
    }

    private CategoryEntity getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(
                        () -> new CategoryNotFoundException("Category not found with id " + categoryId)
                );
    }

    private void validateUniqueIsbn(String isbn, Long bookId) {
        String normalizedIsbn = normalizeIsbn(isbn);

        boolean exists = bookId == null
                ? bookRepository.existsByIsbnIgnoreCase(normalizedIsbn)
                : bookRepository.existsByIsbnIgnoreCaseAndIdNot(
                normalizedIsbn,
                bookId
        );

        if (exists)
            throw new ConflictException("Duplicate isbn");

    }

    private void validateCopyCounts(BookRequest request) {
        if (request.getAvailableCopies() > request.getTotalCopies()) {
            throw new BadRequestException("Insufficient number of copies");
        }
    }

    private String normalizeIsbn(String isbn) {
        return isbn.trim().toUpperCase();
    }
}
