package com.moseshiga.librarymanagement.service.impl;

import com.moseshiga.librarymanagement.dto.BookDto;
import com.moseshiga.librarymanagement.entity.Book;
import com.moseshiga.librarymanagement.repository.BookRepository;
import com.moseshiga.librarymanagement.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    @Override
    public BookDto createBook(BookDto bookDto) {
        Book book = Book.builder()
                .title(bookDto.title())
                .author(bookDto.author())
                .isbn(bookDto.isbn())
                .publicationYear(bookDto.publicationYear())
                .totalCopies(bookDto.totalCopies())
                .availableCopies(bookDto.availableCopies())
                .build();
        Book savedBook = bookRepository.save(book);
        return getBookDto(savedBook);
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book is not found by id: " + id));
        return getBookDto(book);
    }

    @Override
    public List<BookDto> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::getBookDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookDto updateBook(Long id, BookDto bookDto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book is not found by id: " + id));

        book.setTitle(bookDto.title());
        book.setAuthor(bookDto.author());
        book.setIsbn(bookDto.isbn());
        book.setPublicationYear(bookDto.publicationYear());
        book.setTotalCopies(bookDto.totalCopies());
        book.setAvailableCopies(bookDto.availableCopies());

        Book updatedBook = bookRepository.save(book);
        return getBookDto(updatedBook);
    }

    @Override
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    private BookDto getBookDto(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getPublicationYear(),
                book.getTotalCopies(),
                book.getAvailableCopies()
        );
    }
}
