package com.moseshiga.librarymanagement.repository;

import com.moseshiga.librarymanagement.entity.Reader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReaderRepository extends JpaRepository<Reader, Long> {
    boolean existsByEmail(String email);

    Optional<Reader> findByEmail(String email);
}
