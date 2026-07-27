package com.streamforge.controller;

import com.streamforge.dto.request.GenreRequest;
import com.streamforge.dto.response.GenreResponse;
import com.streamforge.service.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenreResponse> createGenre(
            @Valid @RequestBody GenreRequest request) {

        return ResponseEntity.ok(
                genreService.createGenre(request)
        );
    }

    @GetMapping("/{genreId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GenreResponse> getGenreById(
            @PathVariable Long genreId) {

        return ResponseEntity.ok(
                genreService.getGenreById(genreId)
        );
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<GenreResponse>> getAllGenres() {

        return ResponseEntity.ok(
                genreService.getAllGenres()
        );
    }

    @PutMapping("/{genreId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenreResponse> updateGenre(
            @PathVariable Long genreId,
            @Valid @RequestBody GenreRequest request) {

        return ResponseEntity.ok(
                genreService.updateGenre(genreId, request)
        );
    }

    @DeleteMapping("/{genreId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteGenre(
            @PathVariable Long genreId) {

        genreService.deleteGenre(genreId);

        return ResponseEntity.ok("Genre deleted successfully");
    }

}