package com.moseshiga.librarymanagement.service;

import com.moseshiga.librarymanagement.dto.ReaderDto;

import java.util.List;

public interface ReaderService {
    ReaderDto createReader(ReaderDto readerDto);
    ReaderDto getReaderById(Long id);
    List<ReaderDto> getAllReaders();
    ReaderDto updateReader(Long id, ReaderDto readerDto);
    void deleteReader(Long id);
}
