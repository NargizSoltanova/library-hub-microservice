package org.project.userservice.exception;

public class BorrowHistoryNotFoundException extends RuntimeException {
    public BorrowHistoryNotFoundException(String message) {
        super(message);
    }
}
