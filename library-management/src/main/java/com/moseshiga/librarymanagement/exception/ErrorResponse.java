package com.moseshiga.librarymanagement.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse (
        int status,
        String message,
        LocalDateTime timestamp,
        Map<String, String> validationErrors
) {

}
