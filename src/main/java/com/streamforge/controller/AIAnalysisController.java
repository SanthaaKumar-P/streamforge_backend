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


    /* =========================================================
       GET EXISTING AI ANALYSIS
    ========================================================= */

    @GetMapping("/show/{showId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AIAnalysisResponse>
    getAnalysisByShow(
            @PathVariable Long showId
    ) {

        return ResponseEntity.ok(
                aiAnalysisService.getAnalysisByShow(
                        showId
                )
        );
    }


    /* =========================================================
       GENERATE AI PREDICTION
    ========================================================= */

    @PostMapping("/predict/show/{showId}")
    @PreAuthorize(
            "hasAnyRole(" +
                    "'ADMIN'," +
                    "'CONTENT_MANAGER'," +
                    "'CREATOR'" +
                    ")"
    )
    public ResponseEntity<AIAnalysisResponse>
    predictAnalysis(
            @PathVariable Long showId
    ) {

        return ResponseEntity.ok(
                aiAnalysisService.predictAnalysis(
                        showId
                )
        );
    }


    /* =========================================================
       MANUAL AI ANALYSIS
    ========================================================= */

    @PostMapping("/show/{showId}")
    @PreAuthorize(
            "hasAnyRole(" +
                    "'ADMIN'," +
                    "'CONTENT_MANAGER'" +
                    ")"
    )
    public ResponseEntity<AIAnalysisResponse>
    createAnalysis(
            @PathVariable Long showId,
            @RequestBody AIAnalysisResponse request
    ) {

        return ResponseEntity.ok(
                aiAnalysisService.createAnalysis(
                        showId,
                        request
                )
        );
    }
}