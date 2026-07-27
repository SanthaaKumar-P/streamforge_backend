package com.streamforge.controller;

import com.streamforge.dto.request.ShowRequest;
import com.streamforge.dto.response.ShowResponse;
import com.streamforge.service.ShowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shows")
@RequiredArgsConstructor
public class ShowController {

    private final ShowService showService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','CREATOR')")
    public ResponseEntity<ShowResponse> createShow(
            @Valid @RequestBody ShowRequest request) {

        return ResponseEntity.ok(
                showService.createShow(request)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ShowResponse> getShowById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                showService.getShowById(id)
        );
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ShowResponse>> getAllShows() {

        return ResponseEntity.ok(
                showService.getAllShows()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CREATOR')")
    public ResponseEntity<ShowResponse> updateShow(
            @PathVariable Long id,
            @Valid @RequestBody ShowRequest request) {

        return ResponseEntity.ok(
                showService.updateShow(id, request)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteShow(
            @PathVariable Long id) {

        showService.deleteShow(id);

        return ResponseEntity.ok("Show deleted successfully");
    }

}