package com.moseshiga.librarymanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moseshiga.librarymanagement.dto.BookDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class BookControllerSecurityIT {
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getBooks_ShouldBePublic() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk());
    }

    @Test
    void createBook_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        BookDto request = validBookRequest();

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createBook_WithUserRole_ShouldReturnForbidden() throws Exception {
        BookDto request = validBookRequest();

        mockMvc.perform(post("/api/books")
                        .header(HttpHeaders.AUTHORIZATION, basicAuth("user", "user123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createBook_WithAdminRole_ShouldReturnCreated() throws Exception {
        BookDto request = validBookRequest();

        mockMvc.perform(post("/api/books")
                        .header(HttpHeaders.AUTHORIZATION, basicAuth("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void createBook_WithInvalidIsbn_ShouldReturnBadRequest() throws Exception {
        BookDto request = new BookDto(
                null,
                "Invalid ISBN Book",
                "Test Author",
                "invalid-isbn",
                2024,
                1,
                1
        );

        mockMvc.perform(post("/api/books")
                        .header(HttpHeaders.AUTHORIZATION, basicAuth("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBook_WithoutAvailableCopies_ShouldReturnBadRequest() throws Exception {
        BookDto request = new BookDto(
                null,
                "No Available Copies",
                "Test Author",
                generateIsbn(),
                2024,
                1,
                null
        );

        mockMvc.perform(post("/api/books")
                        .header(HttpHeaders.AUTHORIZATION, basicAuth("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    private static BookDto validBookRequest() {
        return new BookDto(
                null,
                "Test Book " + UUID.randomUUID(),
                "Test Author",
                generateIsbn(),
                2024,
                1,
                1
        );
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