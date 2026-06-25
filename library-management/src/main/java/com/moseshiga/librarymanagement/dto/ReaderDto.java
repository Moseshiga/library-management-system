package com.moseshiga.librarymanagement.dto;

import java.time.LocalDate;

public record ReaderDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        LocalDate registrationDate
) {}
