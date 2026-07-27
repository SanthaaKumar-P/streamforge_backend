package com.streamforge.service;

import com.streamforge.dto.response.EvaluationCommentResponse;

import java.util.List;

public interface EvaluationCommentService {

    EvaluationCommentResponse addComment(
            Long evaluationId,
            Long userId,
            String comment
    );

    List<EvaluationCommentResponse> getCommentsByEvaluation(Long evaluationId);

    void deleteComment(Long commentId);

}