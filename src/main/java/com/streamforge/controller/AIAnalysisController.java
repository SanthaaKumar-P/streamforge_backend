package com.streamforge.controller;

import com.streamforge.dto.response.AIAnalysisResponse;
import com.streamforge.service.AIAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai-analysis")
@RequiredArgsConstructor
public class AIAnalysisController {

    private final AIAnalysisService aiAnalysisService;

    @GetMapping("/show/{showId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AIAnalysisResponse> getAnalysisByShow(
            @PathVariable Long showId) {

        return ResponseEntity.ok(
                aiAnalysisService.getAnalysisByShow(showId)
        );
    }

    @PostMapping("/show/{showId}")
    @PreAuthorize("hasAnyRole('ADMIN','CONTENT_MANAGER')")
    public ResponseEntity<AIAnalysisResponse> createAnalysis(
            @PathVariable Long showId,
            @RequestBody AIAnalysisResponse request) {

        return ResponseEntity.ok(
                aiAnalysisService.createAnalysis(showId, request)
        );
    }

}