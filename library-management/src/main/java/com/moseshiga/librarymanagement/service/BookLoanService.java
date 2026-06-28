package com.moseshiga.librarymanagement.service;

import com.moseshiga.librarymanagement.dto.BookLoanDto;
import com.moseshiga.librarymanagement.dto.CreateLoanRequest;

import java.util.List;

public interface BookLoanService {
    BookLoanDto borrowBook(CreateLoanRequest request);
    BookLoanDto returnBook(Long loanId);
    List<BookLoanDto> getAllLoans();
    List<BookLoanDto> getLoansByReaderId(Long readerId);
    List<BookLoanDto> getOverdueLoans();
}
