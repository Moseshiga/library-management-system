package com.moseshiga.librarymanagement.service;

import com.moseshiga.librarymanagement.dto.BookDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {
    BookDto createBook(BookDto bookDto);
    BookDto getBookById(Long id);
    BookDto updateBook(Long id, BookDto bookDto);
    void deleteBook(Long id);
    List<BookDto> searchBooks(String title, String author);
    Page<BookDto> getAllBooks(Pageable pageable);
}
