package com.moseshiga.librarymanagement.service.impl;

import com.moseshiga.librarymanagement.dto.BookDto;
import com.moseshiga.librarymanagement.entity.Book;
import com.moseshiga.librarymanagement.exeption.ConflictException;
import com.moseshiga.librarymanagement.repository.BookLoanRepository;
import com.moseshiga.librarymanagement.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookLoanRepository bookLoanRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void createBook_ShouldSetAvailableCopiesEqualToTotalCopies() {
        // Arrange
        BookDto inputDto = new BookDto(null, "Dune", "Frank Herbert", "1234567890", 1965, 5, 0); // available = 0
        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setTitle("Dune");
        savedBook.setTotalCopies(5);
        savedBook.setAvailableCopies(5);

        when(bookRepository.findByIsbn("1234567890")).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        // Act
        BookDto result = bookService.createBook(inputDto);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.availableCopies());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void createBook_WithExistingIsbn_ShouldThrowConflictException() {
        // Arrange
        BookDto inputDto = new BookDto(null, "Dune", "Frank Herbert", "1234567890", 1965, 5, 5);

        when(bookRepository.findByIsbn("1234567890")).thenReturn(Optional.of(new Book()));

        // Act & Assert
        assertThrows(ConflictException.class, () -> bookService.createBook(inputDto));
        verify(bookRepository, never()).save(any(Book.class));
    }
}
