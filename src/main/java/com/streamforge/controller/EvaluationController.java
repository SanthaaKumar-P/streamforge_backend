package com.streamforge.controller;

import com.streamforge.dto.request.EvaluationRequest;
import com.streamforge.dto.response.EvaluationResponse;
import com.streamforge.service.EvaluationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTENT_MANAGER')")
    public ResponseEntity<EvaluationResponse> createEvaluation(
            @Valid @RequestBody EvaluationRequest request) {

        return ResponseEntity.ok(
                evaluationService.createEvaluation(request)
        );
    }

    @GetMapping("/show/{showId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<EvaluationResponse>> getShowEvaluations(
            @PathVariable Long showId) {

        return ResponseEntity.ok(
                evaluationService.getShowEvaluations(showId)
        );
    }

    @GetMapping("/{evaluationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EvaluationResponse> getEvaluationById(
            @PathVariable Long evaluationId) {

        return ResponseEntity.ok(
                evaluationService.getEvaluationById(evaluationId)
        );
    }

    @PutMapping("/{evaluationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTENT_MANAGER')")
    public ResponseEntity<EvaluationResponse> updateEvaluation(
            @PathVariable Long evaluationId,
            @Valid @RequestBody EvaluationRequest request) {

        return ResponseEntity.ok(
                evaluationService.updateEvaluation(
                        evaluationId,
                        request
                )
        );
    }

    @DeleteMapping("/{evaluationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteEvaluation(
            @PathVariable Long evaluationId) {

        evaluationService.deleteEvaluation(evaluationId);

        return ResponseEntity.ok(
                "Evaluation deleted successfully"
        );
    }
}