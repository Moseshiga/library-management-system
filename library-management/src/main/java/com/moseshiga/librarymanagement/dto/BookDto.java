package com.moseshiga.librarymanagement.dto;

public record BookDto(
        Long id,
        String title,
        String author,
        String isbn,
        Integer publicationYear,
        Integer totalCopies,
        Integer availableCopies
) {
}
