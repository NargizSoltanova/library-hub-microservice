package org.project.userservice.service;

import lombok.AllArgsConstructor;
import org.project.userservice.dto.UserBorrowHistoryDto;
import org.project.userservice.mapper.BorrowHistoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BorrowHistoryService {
    private final UserService userService;
    private final BorrowHistoryMapper borrowHistoryMapper;

    public List<UserBorrowHistoryDto> myBorrowHistory() {
        var user = userService.getUserEntity();
        return user.getBorrowHistories().stream().map(borrowHistoryMapper :: toDto).toList();
    }
}
