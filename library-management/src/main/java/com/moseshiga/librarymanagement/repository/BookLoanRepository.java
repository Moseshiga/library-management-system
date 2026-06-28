package com.moseshiga.librarymanagement.repository;

import com.moseshiga.librarymanagement.entity.BookLoan;
import com.moseshiga.librarymanagement.entity.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface BookLoanRepository extends JpaRepository<BookLoan, Long> {
    Page<BookLoan> findByReaderId(Long readerId, Pageable pageable);

    Page<BookLoan> findByStatusAndDueDateBefore(LoanStatus status, LocalDate currentDatePageable, Pageable pageable);

    long countByStatus(LoanStatus status);

    boolean existsByBookId(Long bookId);

    boolean existsByReaderId(Long readerId);

    long countByStatusAndDueDateBefore(LoanStatus status, LocalDate currentDate);
}
