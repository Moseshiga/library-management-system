package com.moseshiga.librarymanagement.service.impl;

import com.moseshiga.librarymanagement.dto.BookLoanDto;
import com.moseshiga.librarymanagement.entity.Book;
import com.moseshiga.librarymanagement.entity.BookLoan;
import com.moseshiga.librarymanagement.entity.LoanStatus;
import com.moseshiga.librarymanagement.entity.Reader;
import com.moseshiga.librarymanagement.repository.BookLoanRepository;
import com.moseshiga.librarymanagement.repository.BookRepository;
import com.moseshiga.librarymanagement.repository.ReaderRepository;
import com.moseshiga.librarymanagement.service.BookLoanService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor

public class BookLoanServiceImpl implements BookLoanService {
    public static final int STANDARD_LOAN_PERIOD_DAYS = 14;
    private final BookRepository bookRepository;
    private final ReaderRepository readerRepository;
    private final BookLoanRepository bookLoanRepository;

    @Override
    @Transactional
    public BookLoanDto borrowBook(Long bookId, Long readerId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book is not found by id: " + bookId));
        Reader reader = readerRepository.findById(readerId)
                .orElseThrow(() -> new RuntimeException("Reader is not found by id: " + readerId));
        if (book.getAvailableCopies() == 0) {
            throw new RuntimeException("No available copies for book id: " + bookId);
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        BookLoan bookLoan = BookLoan.builder()
                .book(book)
                .reader(reader)
                .loanDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(STANDARD_LOAN_PERIOD_DAYS)) // Standard loan period is 14 days
                .status(LoanStatus.ACTIVE)
                .build();

        BookLoan savedLoan = bookLoanRepository.save(bookLoan);
        return getBookLoanDto(savedLoan);
    }

    @Override
    @Transactional
    public BookLoanDto returnBook(Long loanId) {
        BookLoan bookLoan = bookLoanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan record not found with id: " + loanId));

        if (bookLoan.getStatus() == LoanStatus.RETURNED) {
            throw new RuntimeException("Book is already returned for loan id: " + loanId);
        }

        bookLoan.setReturnDate(LocalDate.now());
        bookLoan.setStatus(LoanStatus.RETURNED);
        bookLoanRepository.save(bookLoan);

        Book book = bookLoan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
        return getBookLoanDto(bookLoan);
    }

    @Override
    public List<BookLoanDto> getAllLoans() {
        return bookLoanRepository.findAll()
                .stream()
                .map(this::getBookLoanDto)
                .toList();
    }

    @Override
    public List<BookLoanDto> getLoansByReaderId(Long readerId) {
        return bookLoanRepository.findByReaderId(readerId).stream()
                .map(this::getBookLoanDto)
                .toList();
    }

    @Override
    public List<BookLoanDto> getOverdueLoans() {
        return bookLoanRepository.findByStatusAndDueDateBefore(LoanStatus.ACTIVE, LocalDate.now())
                .stream()
                .map(this::getBookLoanDto)
                .toList();
    }

    private BookLoanDto getBookLoanDto(BookLoan bookLoan) {
        return new BookLoanDto(
                bookLoan.getId(),
                bookLoan.getBook().getId(),
                bookLoan.getReader().getId(),
                bookLoan.getLoanDate(),
                bookLoan.getDueDate(),
                bookLoan.getReturnDate(),
                bookLoan.getStatus()
                );
    }
}
