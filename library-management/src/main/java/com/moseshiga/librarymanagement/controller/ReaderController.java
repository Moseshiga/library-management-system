package com.moseshiga.librarymanagement.controller;

import com.moseshiga.librarymanagement.dto.ReaderDto;
import com.moseshiga.librarymanagement.service.ReaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/readers")
@RequiredArgsConstructor
public class ReaderController {
    private final ReaderService readerService;

    @PostMapping
    public ResponseEntity<ReaderDto> createReader(@RequestBody ReaderDto readerDto) {
        ReaderDto createdReader = readerService.createReader(readerDto);
        return new ResponseEntity<>(createdReader, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReaderDto> getReaderById(@PathVariable Long id) {
        ReaderDto reader = readerService.getReaderById(id);
        return ResponseEntity.ok(reader);
    }

    @GetMapping
    public ResponseEntity<List<ReaderDto>> getAllReaders() {
        List<ReaderDto> readers = readerService.getAllReaders();
        return ResponseEntity.ok(readers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReaderDto> updateReader(@PathVariable Long id, @RequestBody ReaderDto readerDto) {
        ReaderDto updatedReader = readerService.updateReader(id, readerDto);
        return ResponseEntity.ok(updatedReader);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReader(@PathVariable Long id) {
        readerService.deleteReader(id);
        return ResponseEntity.noContent().build();
    }
}
