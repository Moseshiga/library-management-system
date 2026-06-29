package com.moseshiga.librarymanagement.service.impl;

import com.moseshiga.librarymanagement.dto.BookLoanDto;
import com.moseshiga.librarymanagement.dto.CreateLoanRequest;
import com.moseshiga.librarymanagement.entity.Book;
import com.moseshiga.librarymanagement.entity.BookLoan;
import com.moseshiga.librarymanagement.entity.LoanStatus;
import com.moseshiga.librarymanagement.entity.Reader;
import com.moseshiga.librarymanagement.exception.ConflictException;
import com.moseshiga.librarymanagement.exception.ResourceNotFoundException;
import com.moseshiga.librarymanagement.repository.BookLoanRepository;
import com.moseshiga.librarymanagement.repository.BookRepository;
import com.moseshiga.librarymanagement.repository.ReaderRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookLoanServiceImplTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private ReaderRepository readerRepository;

    @Mock
    private BookLoanRepository bookLoanRepository;

    private BookLoanServiceImpl bookLoanService;

    @BeforeEach
    void setUp() {
        bookLoanService = new BookLoanServiceImpl(
                bookRepository,
                readerRepository,
                bookLoanRepository,
                new SimpleMeterRegistry()
        );
        bookLoanService.initMetrics();
    }

    @Test
    void borrowBook_ShouldDecreaseAvailableCopiesAndCreateLoan() {
        Book book = createBook(1L, 3);
        Reader reader = createReader(1L);
        LocalDate dueDate = LocalDate.now().plusDays(14);

        CreateLoanRequest request = new CreateLoanRequest(
                book.getId(),
                reader.getId(),
                dueDate
        );

        BookLoan savedLoan = BookLoan.builder()
                .id(10L)
                .book(book)
                .reader(reader)
                .loanDate(LocalDate.now())
                .dueDate(dueDate)
                .status(LoanStatus.ACTIVE)
                .build();

        when(bookRepository.findByIdForUpdate(book.getId())).thenReturn(Optional.of(book));
        when(readerRepository.findById(reader.getId())).thenReturn(Optional.of(reader));
        when(bookLoanRepository.save(any(BookLoan.class))).thenReturn(savedLoan);

        BookLoanDto result = bookLoanService.borrowBook(request);

        assertNotNull(result);
        assertEquals(10L, result.id());
        assertEquals(book.getId(), result.bookId());
        assertEquals(reader.getId(), result.readerId());
        assertEquals(dueDate, result.dueDate());
        assertEquals(LoanStatus.ACTIVE, result.status());
        assertEquals(2, book.getAvailableCopies());

        verify(bookRepository).save(book);
        verify(bookLoanRepository).save(any(BookLoan.class));
    }

    @Test
    void borrowBook_WhenNoAvailableCopies_ShouldThrowConflictException() {
        Book book = createBook(1L, 0);
        Reader reader = createReader(1L);

        CreateLoanRequest request = new CreateLoanRequest(
                book.getId(),
                reader.getId(),
                LocalDate.now().plusDays(14)
        );

        when(bookRepository.findByIdForUpdate(book.getId())).thenReturn(Optional.of(book));
        when(readerRepository.findById(reader.getId())).thenReturn(Optional.of(reader));

        assertThrows(ConflictException.class, () -> bookLoanService.borrowBook(request));

        verify(bookRepository, never()).save(any(Book.class));
        verify(bookLoanRepository, never()).save(any(BookLoan.class));
    }

    @Test
    void borrowBook_WhenBookDoesNotExist_ShouldThrowResourceNotFoundException() {
        CreateLoanRequest request = new CreateLoanRequest(
                1L,
                1L,
                LocalDate.now().plusDays(14)
        );

        when(bookRepository.findByIdForUpdate(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookLoanService.borrowBook(request));

        verify(readerRepository, never()).findById(any());
        verify(bookLoanRepository, never()).save(any(BookLoan.class));
    }

    @Test
    void borrowBook_WhenReaderDoesNotExist_ShouldThrowResourceNotFoundException() {
        Book book = createBook(1L, 3);

        CreateLoanRequest request = new CreateLoanRequest(
                book.getId(),
                1L,
                LocalDate.now().plusDays(14)
        );

        when(bookRepository.findByIdForUpdate(book.getId())).thenReturn(Optional.of(book));
        when(readerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookLoanService.borrowBook(request));

        verify(bookLoanRepository, never()).save(any(BookLoan.class));
    }

    @Test
    void returnBook_ShouldMarkLoanAsReturnedAndIncreaseAvailableCopies() {
        Book book = createBook(1L, 2);
        Reader reader = createReader(1L);

        BookLoan loan = BookLoan.builder()
                .id(10L)
                .book(book)
                .reader(reader)
                .loanDate(LocalDate.now().minusDays(3))
                .dueDate(LocalDate.now().plusDays(11))
                .status(LoanStatus.ACTIVE)
                .build();

        when(bookLoanRepository.findById(loan.getId())).thenReturn(Optional.of(loan));
        when(bookLoanRepository.save(any(BookLoan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookLoanDto result = bookLoanService.returnBook(loan.getId());

        assertNotNull(result);
        assertEquals(LoanStatus.RETURNED, result.status());
        assertEquals(LocalDate.now(), result.returnDate());
        assertEquals(3, book.getAvailableCopies());

        verify(bookLoanRepository).save(loan);
        verify(bookRepository).save(book);
    }

    @Test
    void returnBook_WhenLoanAlreadyReturned_ShouldThrowConflictException() {
        Book book = createBook(1L, 2);
        Reader reader = createReader(1L);

        BookLoan loan = BookLoan.builder()
                .id(10L)
                .book(book)
                .reader(reader)
                .loanDate(LocalDate.now().minusDays(3))
                .dueDate(LocalDate.now().plusDays(11))
                .returnDate(LocalDate.now())
                .status(LoanStatus.RETURNED)
                .build();

        when(bookLoanRepository.findById(loan.getId())).thenReturn(Optional.of(loan));

        assertThrows(ConflictException.class, () -> bookLoanService.returnBook(loan.getId()));

        verify(bookLoanRepository, never()).save(any(BookLoan.class));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void returnBook_WhenLoanDoesNotExist_ShouldThrowResourceNotFoundException() {
        when(bookLoanRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookLoanService.returnBook(10L));

        verify(bookLoanRepository, never()).save(any(BookLoan.class));
        verify(bookRepository, never()).save(any(Book.class));
    }

    private static Book createBook(Long id, int availableCopies) {
        return Book.builder()
                .id(id)
                .title("Dune")
                .author("Frank Herbert")
                .isbn("1234567890")
                .publicationYear(1965)
                .totalCopies(5)
                .availableCopies(availableCopies)
                .build();
    }

    private static Reader createReader(Long id) {
        return Reader.builder()
                .id(id)
                .firstName("Paul")
                .lastName("Atreides")
                .email("paul@example.com")
                .phone("+1234567890")
                .registrationDate(LocalDate.now())
                .build();
    }
}
