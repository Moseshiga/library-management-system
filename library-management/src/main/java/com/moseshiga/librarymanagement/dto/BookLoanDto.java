package com.moseshiga.librarymanagement.dto;

import com.moseshiga.librarymanagement.entity.LoanStatus;

import java.time.LocalDate;

public record BookLoanDto (
        Long id,
        Long bookId,
        Long readerId,
        LocalDate loanDate,
        LocalDate dueDate,
        LocalDate returnDate,
        LoanStatus status
){}
