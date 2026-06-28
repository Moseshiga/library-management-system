package com.moseshiga.librarymanagement.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateLoanRequest(
        @NotNull(message = "Book ID is required")
        Long bookId,

        @NotNull(message = "Reader ID is required")
        Long readerId,

        @NotNull(message = "Due date is required")
        @FutureOrPresent(message = "Due date cannot be in the past")
        LocalDate dueDate
) {}
