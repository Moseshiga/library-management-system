package com.moseshiga.librarymanagement.repository;

import com.moseshiga.librarymanagement.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase(String title, String author);
    Optional<Book> findByIsbn(String isbn);
}
