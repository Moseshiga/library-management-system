package com.moseshiga.librarymanagement.service;

import com.moseshiga.librarymanagement.dto.ReaderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReaderService {
    ReaderDto createReader(ReaderDto readerDto);
    ReaderDto getReaderById(Long id);
    Page<ReaderDto> getAllReaders(Pageable pageable);
    ReaderDto updateReader(Long id, ReaderDto readerDto);
    void deleteReader(Long id);
}
