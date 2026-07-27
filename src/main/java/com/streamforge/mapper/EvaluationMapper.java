package com.streamforge.mapper;

import com.streamforge.dto.response.EvaluationResponse;
import com.streamforge.entity.Evaluation;
import org.springframework.stereotype.Component;

@Component
public class EvaluationMapper {

    public EvaluationResponse toResponse(Evaluation evaluation){

        return EvaluationResponse.builder()
                .evaluationId(evaluation.getEvaluationId())
                .originalityScore(evaluation.getOriginalityScore())
                .creativityScore(evaluation.getCreativityScore())
                .marketPotentialScore(evaluation.getMarketPotentialScore())
                .feasibilityScore(evaluation.getFeasibilityScore())
                .overallScore(evaluation.getOverallScore())
                .decision(evaluation.getDecision())
                .remarks(evaluation.getRemarks())
                .build();

    }

}