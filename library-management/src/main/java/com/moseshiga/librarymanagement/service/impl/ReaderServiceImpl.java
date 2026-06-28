package com.moseshiga.librarymanagement.service.impl;

import com.moseshiga.librarymanagement.dto.ReaderDto;
import com.moseshiga.librarymanagement.entity.Reader;
import com.moseshiga.librarymanagement.exeption.ConflictException;
import com.moseshiga.librarymanagement.exeption.ResourceNotFoundException;
import com.moseshiga.librarymanagement.repository.BookLoanRepository;
import com.moseshiga.librarymanagement.repository.ReaderRepository;
import com.moseshiga.librarymanagement.service.ReaderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ReaderServiceImpl implements ReaderService {
    private final ReaderRepository readerRepository;
    private final BookLoanRepository bookLoanRepository;

    @Override
    @Transactional
    public ReaderDto createReader(ReaderDto readerDto) {
        if (readerRepository.existsByEmail(readerDto.email())) {
            throw new ConflictException("Email " + readerDto.email() + " already used");
        }
        Reader reader = new Reader(
                readerDto.id(),
                readerDto.firstName(),
                readerDto.lastName(),
                readerDto.email(),
                readerDto.phone(),
                readerDto.registrationDate()
                );
        reader.setRegistrationDate(LocalDate.now());
        Reader savedReader = readerRepository.save(reader);
        return getReaderDto(savedReader);
    }

    @Override
    public ReaderDto getReaderById(Long id) {
        Reader reader = readerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reader is not found by id: " + id));
        return getReaderDto(reader);
    }

    @Override
    public Page<ReaderDto> getAllReaders(Pageable pageable) {
        return readerRepository.findAll(pageable)
                .map(this::getReaderDto);
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
        if (!readerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reader not found with id: " + id);
        }
        if (bookLoanRepository.existsByReaderId(id)) {
            throw new ConflictException("Cannot delete reader with ID " + id + ". This reader has a loan history in the system.");
        }
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
