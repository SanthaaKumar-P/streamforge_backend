package com.streamforge.controller;

import com.streamforge.dto.response.AIFuturePlannerResponse;
import com.streamforge.service.AIFuturePlannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai-future-planner")
@RequiredArgsConstructor
public class AIFuturePlannerController {

    private final AIFuturePlannerService
            futurePlannerService;


    @GetMapping("/show/{showId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AIFuturePlannerResponse>
    generateForecast(
            @PathVariable Long showId
    ) {

        return ResponseEntity.ok(
                futurePlannerService
                        .generateForecast(
                                showId
                        )
        );
    }
}