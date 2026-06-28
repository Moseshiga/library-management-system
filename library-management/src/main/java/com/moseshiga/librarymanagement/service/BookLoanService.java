package com.moseshiga.librarymanagement.service;

import com.moseshiga.librarymanagement.dto.BookLoanDto;
import com.moseshiga.librarymanagement.dto.CreateLoanRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookLoanService {
    BookLoanDto borrowBook(CreateLoanRequest request);
    BookLoanDto returnBook(Long loanId);
    Page<BookLoanDto> getAllLoans(Pageable pageable);
    Page<BookLoanDto> getLoansByReaderId(Long readerId, Pageable pageable);
    Page<BookLoanDto> getOverdueLoans(Pageable pageable);
}
