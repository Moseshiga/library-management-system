package com.moseshiga.librarymanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

public record BookDto(
        Long id,
        @NotBlank(message = "Book title is required")
        String title,

        @NotBlank(message = "Author name is required")
        String author,
        @NotBlank(message = "ISBN is required")
        @Pattern(regexp = "^(\\d{10}|\\d{13})$", message = "ISBN must contain exactly 10 or 13 digits")
        String isbn,

        @NotNull(message = "Publication year is required")
        Integer publicationYear,
        @NotNull(message = "Total copies count is required")
        @PositiveOrZero(message = "Total copies cannot be negative")
        Integer totalCopies,
        @PositiveOrZero(message = "Available copies cannot be negative")
        Integer availableCopies
) {
}
