package com.moseshiga.librarymanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.moseshiga.librarymanagement.dto.CreateLoanRequest;
import com.moseshiga.librarymanagement.entity.Book;
import com.moseshiga.librarymanagement.entity.Reader;
import com.moseshiga.librarymanagement.repository.BookLoanRepository;
import com.moseshiga.librarymanagement.repository.BookRepository;
import com.moseshiga.librarymanagement.repository.ReaderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;
import java.util.UUID;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class BookLoanControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReaderRepository readerRepository;

    @Autowired
    private BookLoanRepository bookLoanRepository;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        bookLoanRepository.deleteAll();
        bookRepository.deleteAll();
        readerRepository.deleteAll();
    }

    @Test
    void borrowBook_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        CreateLoanRequest request = new CreateLoanRequest(
                1L,
                1L,
                LocalDate.now().plusDays(7)
        );

        mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void borrowBook_WithPastDueDate_ShouldReturnBadRequest() throws Exception {
        Book book = bookRepository.save(createBook(2));
        Reader reader = readerRepository.save(createReader());

        CreateLoanRequest request = new CreateLoanRequest(
                book.getId(),
                reader.getId(),
                LocalDate.now().minusDays(1)
        );

        mockMvc.perform(post("/api/loans")
                        .header(HttpHeaders.AUTHORIZATION, basicAuth("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void borrowBook_WithAdminRole_ShouldReturnCreated() throws Exception {
        Book book = bookRepository.save(createBook(2));
        Reader reader = readerRepository.save(createReader());

        CreateLoanRequest request = new CreateLoanRequest(
                book.getId(),
                reader.getId(),
                LocalDate.now().plusDays(7)
        );

        mockMvc.perform(post("/api/loans")
                        .header(HttpHeaders.AUTHORIZATION, basicAuth("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.bookId").value(book.getId()))
                .andExpect(jsonPath("$.readerId").value(reader.getId()))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void borrowBook_WhenNoAvailableCopies_ShouldReturnConflict() throws Exception {
        Book book = bookRepository.save(createBook(0));
        Reader reader = readerRepository.save(createReader());

        CreateLoanRequest request = new CreateLoanRequest(
                book.getId(),
                reader.getId(),
                LocalDate.now().plusDays(7)
        );

        mockMvc.perform(post("/api/loans")
                        .header(HttpHeaders.AUTHORIZATION, basicAuth("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void returnBook_WithAdminRole_ShouldReturnOk() throws Exception {
        Book book = bookRepository.save(createBook(1));
        Reader reader = readerRepository.save(createReader());

        CreateLoanRequest request = new CreateLoanRequest(
                book.getId(),
                reader.getId(),
                LocalDate.now().plusDays(7)
        );

        String response = mockMvc.perform(post("/api/loans")
                        .header(HttpHeaders.AUTHORIZATION, basicAuth("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long loanId = objectMapper.readTree(response)
                .get("id")
                .asLong();

        mockMvc.perform(put("/api/loans/{id}/return", loanId)
                        .header(HttpHeaders.AUTHORIZATION, basicAuth("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(loanId))
                .andExpect(jsonPath("$.status").value("RETURNED"))
                .andExpect(jsonPath("$.returnDate").value(LocalDate.now().toString()));
    }

    @Test
    void returnBook_WhenAlreadyReturned_ShouldReturnConflict() throws Exception {
        Book book = bookRepository.save(createBook(1));
        Reader reader = readerRepository.save(createReader());

        CreateLoanRequest request = new CreateLoanRequest(
                book.getId(),
                reader.getId(),
                LocalDate.now().plusDays(7)
        );

        String response = mockMvc.perform(post("/api/loans")
                        .header(HttpHeaders.AUTHORIZATION, basicAuth("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long loanId = objectMapper.readTree(response)
                .get("id")
                .asLong();

        mockMvc.perform(put("/api/loans/{id}/return", loanId)
                        .header(HttpHeaders.AUTHORIZATION, basicAuth("admin", "admin123")))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/loans/{id}/return", loanId)
                        .header(HttpHeaders.AUTHORIZATION, basicAuth("admin", "admin123")))
                .andExpect(status().isConflict());
    }

    private static Book createBook(int availableCopies) {
        return Book.builder()
                .title("Book " + UUID.randomUUID())
                .author("Test Author")
                .isbn(generateIsbn())
                .publicationYear(2024)
                .totalCopies(Math.max(availableCopies, 1))
                .availableCopies(availableCopies)
                .build();
    }

    private static Reader createReader() {
        return Reader.builder()
                .firstName("Paul")
                .lastName("Atreides")
                .email("reader-" + UUID.randomUUID() + "@example.com")
                .phone("+1234567890")
                .registrationDate(LocalDate.now())
                .build();
    }

    private static String generateIsbn() {
        return String.valueOf(1_000_000_000L + Math.abs(UUID.randomUUID().getMostSignificantBits() % 8_999_999_999L));
    }

    private static String basicAuth(String username, String password) {
        String credentials = username + ":" + password;
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        return "Basic " + encoded;
    }
}