package org.example.bookservice.exception;

public class BorrowNotFoundException extends RuntimeException {
    public BorrowNotFoundException(String message) {
        super(message);
    }
}
