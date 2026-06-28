package com.moseshiga.librarymanagement.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class BookControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllBooks_ShouldReturnPaginatedList() throws Exception {
        mockMvc.perform(get("/api/books?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.pageable").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getBookById_WhenBookDoesNotExist_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/books/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found with id: 9999"));
    }
}