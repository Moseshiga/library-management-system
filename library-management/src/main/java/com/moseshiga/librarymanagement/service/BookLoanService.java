package com.moseshiga.librarymanagement.service;

import com.moseshiga.librarymanagement.dto.BookLoanDto;

import java.util.List;

public interface BookLoanService {
    BookLoanDto borrowBook(Long bookId, Long readerId);
    BookLoanDto returnBook(Long loanId);
    List<BookLoanDto> getAllLoans();
}
