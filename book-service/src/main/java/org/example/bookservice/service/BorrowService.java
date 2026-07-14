package org.example.bookservice.service;

import lombok.RequiredArgsConstructor;
import org.example.bookservice.constant.BorrowStatus;
import org.example.bookservice.dto.BorrowResponse;
import org.example.bookservice.entity.BorrowEntity;
import org.example.bookservice.event.BorrowEvent;
import org.example.bookservice.exception.BookNotFoundException;
import org.example.bookservice.exception.BorrowNotFoundException;
import org.example.bookservice.exception.ConflictException;
import org.example.bookservice.mapper.BorrowMapper;
import org.example.bookservice.repository.BookRepository;
import org.example.bookservice.repository.BorrowRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowService {
    private final BorrowRepository borrowRepository;
    private final BookRepository bookRepository;
    private final BorrowMapper borrowMapper;
    private final ApplicationEventPublisher eventPublisher;

    private static final List<BorrowStatus> ACTIVE_STATUSES =
            List.of(BorrowStatus.BORROWED, BorrowStatus.OVERDUE);


    @Transactional
    @CacheEvict(value = "books", allEntries = true)
    public BorrowResponse borrow(Long bookId, Long userId) {
        validateUserHasNotBorrowedBook(userId, bookId);

        var book = bookRepository.findActiveByIdForUpdate(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found "));

        if (book.getAvailableCopies() == 0)
            throw new ConflictException("Book has no available copies");

        book.setAvailableCopies(book.getAvailableCopies() - 1);

        var borrow = BorrowEntity.builder()
                .book(book)
                .userId(userId)
                .build();

        var savedBorrow = borrowRepository.save(borrow);

        eventPublisher.publishEvent(
                new BorrowEvent(
                        savedBorrow.getId(),
                        userId,
                        book.getId(),
                        book.getTitle(),
                        savedBorrow.getBorrowedAt(),
                        null,
                        BorrowStatus.BORROWED
                )
        );

        return borrowMapper.toResponse(savedBorrow);
    }

    @Transactional
    @CacheEvict(value = "books", allEntries = true)
    public BorrowResponse returnBook(Long borrowId, Long userId) {

        var borrow = borrowRepository.findByIdAndUserIdAndStatusIn(borrowId, userId, ACTIVE_STATUSES)
                .orElseThrow(() -> new BorrowNotFoundException("Borrow not found"));

        var book = bookRepository.findActiveByIdForUpdate(borrow.getBook().getId())
                .orElseThrow(() -> new BookNotFoundException("Book not found "));

        borrow.setStatus(BorrowStatus.RETURNED);
        borrow.setReturnedAt(LocalDateTime.now());
        book.setAvailableCopies(book.getAvailableCopies() + 1);

        var savedBorrow = borrowRepository.save(borrow);

        eventPublisher.publishEvent(
                new BorrowEvent(
                        savedBorrow.getId(),
                        userId,
                        book.getId(),
                        book.getTitle(),
                        savedBorrow.getBorrowedAt(),
                        savedBorrow.getReturnedAt(),
                        BorrowStatus.RETURNED
                )
        );

        return borrowMapper.toResponse(savedBorrow);
    }

    @Transactional(readOnly = true)
    public Page<BorrowResponse> getMyBorrows(Long userId, Pageable pageable) {
        return borrowRepository.findAllByUserId(userId, pageable).map(borrowMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<BorrowResponse> getAllBorrows(Pageable pageable) {
        return borrowRepository.findAllBy(pageable).map(borrowMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<BorrowResponse> getOverdueBorrows(Pageable pageable) {
        return borrowRepository.findAllByStatus(BorrowStatus.OVERDUE, pageable)
                .map(borrowMapper::toResponse);
    }

    private void validateUserHasNotBorrowedBook(Long userId, Long bookId) {
        boolean alreadyBorrowed = borrowRepository
                        .existsByUserIdAndBookIdAndStatusIn(userId, bookId, ACTIVE_STATUSES);

        if (alreadyBorrowed) {
            throw new ConflictException("Book has already been borrowed.");
        }
    }
}
