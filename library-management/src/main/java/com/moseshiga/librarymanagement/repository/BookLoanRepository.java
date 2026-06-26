package com.moseshiga.librarymanagement.repository;

import com.moseshiga.librarymanagement.entity.BookLoan;
import com.moseshiga.librarymanagement.entity.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookLoanRepository extends JpaRepository<BookLoan, Long> {
    List<BookLoan> findByReaderId(Long readerId);

    List<BookLoan> findByStatusAndDueDateBefore(LoanStatus status, LocalDate currentDate);
}
