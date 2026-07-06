package org.example.bookservice.service;

import lombok.RequiredArgsConstructor;
import org.example.bookservice.repository.BookRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
}
