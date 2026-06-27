package com.moseshiga.librarymanagement.service.impl;

import com.moseshiga.librarymanagement.dto.StatisticsDto;
import com.moseshiga.librarymanagement.entity.LoanStatus;
import com.moseshiga.librarymanagement.repository.BookLoanRepository;
import com.moseshiga.librarymanagement.repository.BookRepository;
import com.moseshiga.librarymanagement.repository.ReaderRepository;
import com.moseshiga.librarymanagement.service.StatisticsService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final BookRepository bookRepository;
    private final ReaderRepository readerRepository;
    private final BookLoanRepository bookLoanRepository;

    @Override
    @Transactional(readOnly = true)
    public StatisticsDto getLibraryStatistics() {
        long totalBooks = bookRepository.count();
        long totalReaders = readerRepository.count();
        long activeLoans = bookLoanRepository.countByStatus(LoanStatus.ACTIVE);

        return new StatisticsDto(totalBooks, totalReaders, activeLoans);
    }
}
