package org.example.bookservice.service;

import lombok.RequiredArgsConstructor;
import org.example.bookservice.repository.BorrowRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BorrowService {
    private final BorrowRepository borrowRepository;
}
