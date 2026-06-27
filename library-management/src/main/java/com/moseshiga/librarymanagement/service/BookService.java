package com.moseshiga.librarymanagement.service;

import com.moseshiga.librarymanagement.dto.BookDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto createBook(BookDto bookDto);
    BookDto getBookById(Long id);
    BookDto updateBook(Long id, BookDto bookDto);
    void deleteBook(Long id);
    Page<BookDto> searchBooks(String title, String author, Integer year, Boolean available, Pageable pageable);
    Page<BookDto> getAllBooks(Pageable pageable);
}
