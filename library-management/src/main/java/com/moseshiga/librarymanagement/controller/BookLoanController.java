package com.moseshiga.librarymanagement.controller;

import com.moseshiga.librarymanagement.dto.BookLoanDto;
import com.moseshiga.librarymanagement.dto.CreateLoanRequest;
import com.moseshiga.librarymanagement.service.BookLoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class BookLoanController {
    private final BookLoanService bookLoanService;

    @PostMapping("/borrow")
    public ResponseEntity<BookLoanDto> borrowBook(
            @Valid @RequestBody CreateLoanRequest createLoanRequest
            ) {
        BookLoanDto loan = bookLoanService.borrowBook(createLoanRequest);
        return new ResponseEntity<>(loan, HttpStatus.CREATED);
    }

    @PutMapping("{id}/return")
    public ResponseEntity<BookLoanDto> returnBook(
            @PathVariable Long id
    ) {
        BookLoanDto loan = bookLoanService.returnBook(id);
        return ResponseEntity.ok(loan);
    }

    @GetMapping("/reader/{readerId}")
    public ResponseEntity<List<BookLoanDto>> getReaderHistory(@PathVariable Long readerId) {
        return ResponseEntity.ok(bookLoanService.getLoansByReaderId(readerId));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<BookLoanDto>> getOverdueLoans() {
        return ResponseEntity.ok(bookLoanService.getOverdueLoans());
    }

    @GetMapping
    public ResponseEntity<List<BookLoanDto>> getAllLoans() {
        List<BookLoanDto> allLoans = bookLoanService.getAllLoans();
        return ResponseEntity.ok(allLoans);
    }
}
