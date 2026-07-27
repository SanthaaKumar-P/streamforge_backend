package com.streamforge.mapper;

import com.streamforge.dto.response.EvaluationCommentResponse;
import com.streamforge.entity.EvaluationComment;
import org.springframework.stereotype.Component;

@Component
public class EvaluationCommentMapper {

    public EvaluationCommentResponse toResponse(EvaluationComment comment){

        return EvaluationCommentResponse.builder()
                .commentId(comment.getCommentId())
                .comment(comment.getComment())
                .evaluationId(comment.getEvaluation().getEvaluationId())
                .userId(comment.getUser().getUserId())
                .build();

    }

}