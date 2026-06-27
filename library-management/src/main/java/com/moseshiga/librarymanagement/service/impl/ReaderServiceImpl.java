package com.moseshiga.librarymanagement.service.impl;

import com.moseshiga.librarymanagement.dto.ReaderDto;
import com.moseshiga.librarymanagement.entity.Reader;
import com.moseshiga.librarymanagement.exeption.ResourceNotFoundException;
import com.moseshiga.librarymanagement.repository.ReaderRepository;
import com.moseshiga.librarymanagement.service.ReaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReaderServiceImpl implements ReaderService {
    private final ReaderRepository readerRepository;

    @Override
    public ReaderDto createReader(ReaderDto readerDto) {
        Reader reader = new Reader(
                readerDto.id(),
                readerDto.firstName(),
                readerDto.lastName(),
                readerDto.email(),
                readerDto.phone(),
                readerDto.registrationDate()
                );
        readerRepository.save(reader);
        return getReaderDto(reader);
    }

    @Override
    public ReaderDto getReaderById(Long id) {
        Reader reader = readerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reader is not found by id: " + id));
        return getReaderDto(reader);
    }

    @Override
    public List<ReaderDto> getAllReaders() {
        return readerRepository.findAll()
                .stream()
                .map(this::getReaderDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReaderDto updateReader(Long id, ReaderDto readerDto) {
        Reader reader = readerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reader is not found by id: " + id));
        reader.setFirstName(readerDto.firstName());
        reader.setLastName(readerDto.lastName());
        reader.setEmail(readerDto.email());
        reader.setPhone(readerDto.phone());
        reader.setRegistrationDate(readerDto.registrationDate());

        Reader updatedReader = readerRepository.save(reader);
        return getReaderDto(updatedReader);
    }

    @Override
    public void deleteReader(Long id) {
        readerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reader is not found by id: " + id));
        readerRepository.deleteById(id);
    }

    private ReaderDto getReaderDto(Reader reader) {
        return new ReaderDto(
                reader.getId(),
                reader.getFirstName(),
                reader.getLastName(),
                reader.getEmail(),
                reader.getPhone(),
                reader.getRegistrationDate()
        );
    }
}
