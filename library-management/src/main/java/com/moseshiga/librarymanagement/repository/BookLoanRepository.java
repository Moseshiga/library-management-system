package com.moseshiga.librarymanagement.repository;

import com.moseshiga.librarymanagement.entity.BookLoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookLoanRepository extends JpaRepository<BookLoan, Long> {
}
