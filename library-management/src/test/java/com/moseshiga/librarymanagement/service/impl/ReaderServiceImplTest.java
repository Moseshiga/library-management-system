package com.moseshiga.librarymanagement.service.impl;

import com.moseshiga.librarymanagement.dto.ReaderDto;
import com.moseshiga.librarymanagement.entity.Reader;
import com.moseshiga.librarymanagement.exeption.ConflictException;
import com.moseshiga.librarymanagement.exeption.ResourceNotFoundException;
import com.moseshiga.librarymanagement.repository.BookLoanRepository;
import com.moseshiga.librarymanagement.repository.ReaderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReaderServiceImplTest {

    @Mock
    private ReaderRepository readerRepository;

    @Mock
    private BookLoanRepository bookLoanRepository;

    @InjectMocks
    private ReaderServiceImpl readerService;

    @Test
    void createReader_ShouldCreateReaderWithCurrentRegistrationDate() {
        ReaderDto request = new ReaderDto(
                null,
                "Paul",
                "Atreides",
                "paul@example.com",
                "+1234567890",
                LocalDate.of(2000, 1, 1)
        );

        Reader savedReader = createReader(
                1L,
                "Paul",
                "Atreides",
                "paul@example.com",
                "+1234567890",
                LocalDate.now()
        );

        when(readerRepository.existsByEmail(request.email())).thenReturn(false);
        when(readerRepository.save(any(Reader.class))).thenReturn(savedReader);

        ReaderDto result = readerService.createReader(request);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Paul", result.firstName());
        assertEquals("Atreides", result.lastName());
        assertEquals("paul@example.com", result.email());
        assertEquals(LocalDate.now(), result.registrationDate());

        verify(readerRepository).save(any(Reader.class));
    }

    @Test
    void createReader_WhenEmailAlreadyExists_ShouldThrowConflictException() {
        ReaderDto request = new ReaderDto(
                null,
                "Paul",
                "Atreides",
                "paul@example.com",
                "+1234567890",
                null
        );

        when(readerRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(ConflictException.class, () -> readerService.createReader(request));

        verify(readerRepository, never()).save(any(Reader.class));
    }

    @Test
    void updateReader_ShouldUpdateReaderWithoutChangingRegistrationDate() {
        Long readerId = 1L;
        LocalDate originalRegistrationDate = LocalDate.of(2024, 1, 10);

        Reader existingReader = createReader(
                readerId,
                "Paul",
                "Atreides",
                "paul@example.com",
                "+1234567890",
                originalRegistrationDate
        );

        ReaderDto request = new ReaderDto(
                readerId,
                "Leto",
                "Atreides",
                "leto@example.com",
                "+9876543210",
                LocalDate.of(2030, 1, 1)
        );

        when(readerRepository.findById(readerId)).thenReturn(Optional.of(existingReader));
        when(readerRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(readerRepository.save(existingReader)).thenReturn(existingReader);

        ReaderDto result = readerService.updateReader(readerId, request);

        assertNotNull(result);
        assertEquals(readerId, result.id());
        assertEquals("Leto", result.firstName());
        assertEquals("Atreides", result.lastName());
        assertEquals("leto@example.com", result.email());
        assertEquals("+9876543210", result.phone());
        assertEquals(originalRegistrationDate, result.registrationDate());

        verify(readerRepository).save(existingReader);
    }

    @Test
    void updateReader_WhenReaderDoesNotExist_ShouldThrowResourceNotFoundException() {
        Long readerId = 1L;

        ReaderDto request = new ReaderDto(
                readerId,
                "Paul",
                "Atreides",
                "paul@example.com",
                "+1234567890",
                null
        );

        when(readerRepository.findById(readerId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> readerService.updateReader(readerId, request));

        verify(readerRepository, never()).save(any(Reader.class));
    }

    @Test
    void updateReader_WhenEmailBelongsToAnotherReader_ShouldThrowConflictException() {
        Long readerId = 1L;

        Reader existingReader = createReader(
                readerId,
                "Paul",
                "Atreides",
                "paul@example.com",
                "+1234567890",
                LocalDate.of(2024, 1, 10)
        );

        Reader readerWithSameEmail = createReader(
                2L,
                "Leto",
                "Atreides",
                "leto@example.com",
                "+9876543210",
                LocalDate.of(2024, 1, 11)
        );

        ReaderDto request = new ReaderDto(
                readerId,
                "Paul",
                "Atreides",
                "leto@example.com",
                "+1234567890",
                null
        );

        when(readerRepository.findById(readerId)).thenReturn(Optional.of(existingReader));
        when(readerRepository.findByEmail(request.email())).thenReturn(Optional.of(readerWithSameEmail));

        assertThrows(ConflictException.class, () -> readerService.updateReader(readerId, request));

        verify(readerRepository, never()).save(any(Reader.class));
    }

    @Test
    void updateReader_WhenEmailBelongsToSameReader_ShouldUpdateReader() {
        Long readerId = 1L;

        Reader existingReader = createReader(
                readerId,
                "Paul",
                "Atreides",
                "paul@example.com",
                "+1234567890",
                LocalDate.of(2024, 1, 10)
        );

        ReaderDto request = new ReaderDto(
                readerId,
                "Paul",
                "MuadDib",
                "paul@example.com",
                "+1234567890",
                null
        );

        when(readerRepository.findById(readerId)).thenReturn(Optional.of(existingReader));
        when(readerRepository.findByEmail(request.email())).thenReturn(Optional.of(existingReader));
        when(readerRepository.save(existingReader)).thenReturn(existingReader);

        ReaderDto result = readerService.updateReader(readerId, request);

        assertNotNull(result);
        assertEquals("MuadDib", result.lastName());
        assertEquals("paul@example.com", result.email());

        verify(readerRepository).save(existingReader);
    }

    @Test
    void deleteReader_ShouldDeleteReader_WhenReaderExistsAndHasNoLoanHistory() {
        Long readerId = 1L;

        when(readerRepository.existsById(readerId)).thenReturn(true);
        when(bookLoanRepository.existsByReaderId(readerId)).thenReturn(false);

        readerService.deleteReader(readerId);

        verify(readerRepository).deleteById(readerId);
    }

    @Test
    void deleteReader_WhenReaderDoesNotExist_ShouldThrowResourceNotFoundException() {
        Long readerId = 1L;

        when(readerRepository.existsById(readerId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> readerService.deleteReader(readerId));

        verify(readerRepository, never()).deleteById(readerId);
    }

    @Test
    void deleteReader_WhenReaderHasLoanHistory_ShouldThrowConflictException() {
        Long readerId = 1L;

        when(readerRepository.existsById(readerId)).thenReturn(true);
        when(bookLoanRepository.existsByReaderId(readerId)).thenReturn(true);

        assertThrows(ConflictException.class, () -> readerService.deleteReader(readerId));

        verify(readerRepository, never()).deleteById(readerId);
    }

    private static Reader createReader(
            Long id,
            String firstName,
            String lastName,
            String email,
            String phone,
            LocalDate registrationDate
    ) {
        return Reader.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone)
                .registrationDate(registrationDate)
                .build();
    }
}