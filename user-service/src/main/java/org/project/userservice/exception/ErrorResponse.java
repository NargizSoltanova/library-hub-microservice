package org.project.userservice.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        int status,
        LocalDateTime timeStamp
){
}
