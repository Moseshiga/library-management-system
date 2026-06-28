package com.moseshiga.librarymanagement.service.impl;

import com.moseshiga.librarymanagement.dto.BookDto;
import com.moseshiga.librarymanagement.entity.Book;
import com.moseshiga.librarymanagement.exeption.ConflictException;
import com.moseshiga.librarymanagement.exeption.ResourceNotFoundException;
import com.moseshiga.librarymanagement.repository.BookRepository;
import com.moseshiga.librarymanagement.service.BookService;
import com.moseshiga.librarymanagement.specification.BookSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    @Override
    public BookDto createBook(BookDto bookDto) {
        if (bookRepository.findByIsbn(bookDto.isbn()).isPresent()) {
            throw new ConflictException("Book with ISBN " + bookDto.isbn() + " already exists in the catalog.");
        }
        Book book = mapToEntity(bookDto);
        Book savedBook = bookRepository.save(book);
        return getBookDto(savedBook);
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book is not found by id: " + id));
        return getBookDto(book);
    }

    @Override
    public BookDto updateBook(Long id, BookDto bookDto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book is not found by id: " + id));

        bookRepository.findByIsbn(bookDto.isbn())
                .ifPresent(bookWithSameIsbn -> {
                    if (!bookWithSameIsbn.getId().equals(id)) {
                        throw new ConflictException("Another book with ISBN " + bookDto.isbn() + " already exists.");
                    }
                });

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

    @Override
    @Transactional(readOnly = true)
    public Page<BookDto> searchBooks(String title,
                                     String author,
                                     String isbn,
                                     Integer year,
                                     Boolean available,
                                     Pageable pageable) {
        Specification<Book> spec = Specification
                .where(BookSpecifications.hasTitle(title))
                .and(BookSpecifications.hasAuthor(author))
                .and(BookSpecifications.hasIsbn(isbn))
                .and(BookSpecifications.hasPublicationYear(year))
                .and(BookSpecifications.isAvailable(available));

        return bookRepository.findAll(spec, pageable)
                .map(this::getBookDto);
    }

    @Override
    public Page<BookDto> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(this::getBookDto);
    }


    private static Book mapToEntity(BookDto bookDto) {
        return Book.builder()
                .title(bookDto.title())
                .author(bookDto.author())
                .isbn(bookDto.isbn())
                .publicationYear(bookDto.publicationYear())
                .totalCopies(bookDto.totalCopies())
                .availableCopies(bookDto.availableCopies())
                .build();
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
