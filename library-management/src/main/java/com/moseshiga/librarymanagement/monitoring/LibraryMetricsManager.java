package com.moseshiga.librarymanagement.monitoring;

import com.moseshiga.librarymanagement.entity.LoanStatus;
import com.moseshiga.librarymanagement.repository.BookLoanRepository;
import com.moseshiga.librarymanagement.repository.ReaderRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class LibraryMetricsManager {
    private final MeterRegistry meterRegistry;
    private final ReaderRepository readerRepository;
    private final BookLoanRepository bookLoanRepository;

    @PostConstruct
    public void initMetrics() {
        Gauge.builder("library.readers.total", readerRepository, ReaderRepository::count)
                .description("Total number of registered readers")
                .register(meterRegistry);

        Gauge.builder("library.loans.overdue", bookLoanRepository,
                        repo -> repo.countByStatusAndDueDateBefore(LoanStatus.ACTIVE, LocalDate.now()))
                .description("Total number of overdue book loans")
                .register(meterRegistry);
    }
}
