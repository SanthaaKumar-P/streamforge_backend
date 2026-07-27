package com.streamforge.controller;

import com.streamforge.dto.request.EpisodeRequest;
import com.streamforge.dto.response.EpisodeResponse;
import com.streamforge.service.EpisodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/episodes")
@RequiredArgsConstructor
public class EpisodeController {

    private final EpisodeService episodeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','CREATOR')")
    public ResponseEntity<EpisodeResponse> createEpisode(
            @Valid @RequestBody EpisodeRequest request) {

        return ResponseEntity.ok(
                episodeService.createEpisode(request)
        );
    }

    @GetMapping("/show/{showId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<EpisodeResponse>> getEpisodesByShow(
            @PathVariable Long showId) {

        return ResponseEntity.ok(
                episodeService.getEpisodesByShow(showId)
        );
    }

    @GetMapping("/{episodeId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EpisodeResponse> getEpisodeById(
            @PathVariable Long episodeId) {

        return ResponseEntity.ok(
                episodeService.getEpisodeById(episodeId)
        );
    }

    @PutMapping("/{episodeId}")
    @PreAuthorize("hasAnyRole('ADMIN','CREATOR')")
    public ResponseEntity<EpisodeResponse> updateEpisode(
            @PathVariable Long episodeId,
            @Valid @RequestBody EpisodeRequest request) {

        return ResponseEntity.ok(
                episodeService.updateEpisode(episodeId, request)
        );
    }

    @DeleteMapping("/{episodeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteEpisode(
            @PathVariable Long episodeId) {

        episodeService.deleteEpisode(episodeId);

        return ResponseEntity.ok("Episode deleted successfully");
    }

}