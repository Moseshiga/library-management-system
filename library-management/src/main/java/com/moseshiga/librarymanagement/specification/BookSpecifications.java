package com.moseshiga.librarymanagement.specification;

import com.moseshiga.librarymanagement.entity.Book;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecifications {
    public static Specification<Book> hasTitle(String title) {
        return (root, query, criteriaBuilder) -> {
            if (title == null || title.isBlank()) return null;
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
        };
    }

    public static Specification<Book> hasAuthor(String author) {
        return (root, query, criteriaBuilder) -> {
            if (author == null || author.isBlank()) return null;
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("author")), "%" + author.toLowerCase() + "%");
        };
    }

    public static Specification<Book> hasPublicationYear(Integer year) {
        return (root, query, criteriaBuilder) -> {
            if (year == null) return null;
            return criteriaBuilder.equal(root.get("publicationYear"), year);
        };
    }

    public static Specification<Book> isAvailable(Boolean available) {
        return (root, query, criteriaBuilder) -> {
            if (available == null) return null;
            if (available) {
                return criteriaBuilder.greaterThan(root.get("availableCopies"), 0);
            } else {
                return criteriaBuilder.equal(root.get("availableCopies"), 0);
            }
        };
    }

    public static Specification<Book> hasIsbn(String isbn) {
        return (root, query, criteriaBuilder) -> {
            if (isbn == null || isbn.isBlank()) return null;
            return criteriaBuilder.equal(root.get("isbn"), isbn);
        };
    }
}
