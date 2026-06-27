package com.moseshiga.librarymanagement.dto;

public record StatisticsDto(
        long totalBooks,
        long totalReaders,
        long activeLoans
) {}