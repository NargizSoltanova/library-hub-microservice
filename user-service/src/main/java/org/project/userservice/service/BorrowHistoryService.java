package org.project.userservice.service;

import lombok.AllArgsConstructor;
import org.project.userservice.repository.BorrowHistoryRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BorrowHistoryService {
    private final BorrowHistoryRepository borrowHistoryRepository;
}
