package com.moseshiga.librarymanagement.exeption;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
