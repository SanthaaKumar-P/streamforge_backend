package com.streamforge.controller;

import com.streamforge.dto.request.EvaluationCommentRequest;
import com.streamforge.dto.response.EvaluationCommentResponse;
import com.streamforge.service.EvaluationCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluation-comments")
@RequiredArgsConstructor
public class EvaluationCommentController {

    private final EvaluationCommentService commentService;


    // =========================================================
    // ADD COMMENT
    // =========================================================

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTENT_MANAGER')")
    public ResponseEntity<EvaluationCommentResponse> addComment(
            @Valid @RequestBody EvaluationCommentRequest request) {

        return ResponseEntity.ok(
                commentService.addComment(
                        request.getEvaluationId(),
                        request.getUserId(),
                        request.getComment()
                )
        );
    }


    // =========================================================
    // GET COMMENTS BY EVALUATION
    // =========================================================

    @GetMapping("/evaluation/{evaluationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<EvaluationCommentResponse>>
    getCommentsByEvaluation(
            @PathVariable Long evaluationId) {

        return ResponseEntity.ok(
                commentService.getCommentsByEvaluation(
                        evaluationId
                )
        );
    }


    // =========================================================
    // DELETE COMMENT
    // =========================================================

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteComment(
            @PathVariable Long commentId) {

        commentService.deleteComment(
                commentId
        );

        return ResponseEntity.ok(
                "Evaluation comment deleted successfully"
        );
    }
}