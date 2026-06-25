package com.moseshiga.librarymanagement.repository;

import com.moseshiga.librarymanagement.entity.Reader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReaderRepository extends JpaRepository<Reader, Long> {
}
