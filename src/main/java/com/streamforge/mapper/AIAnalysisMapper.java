package com.streamforge.mapper;

import com.streamforge.dto.response.AIAnalysisResponse;
import com.streamforge.entity.AIAnalysis;
import org.springframework.stereotype.Component;

@Component
public class AIAnalysisMapper {

    public AIAnalysisResponse toResponse(AIAnalysis analysis){

        return AIAnalysisResponse.builder()
                .analysisId(analysis.getAnalysisId())
                .summary(analysis.getSummary())
                .predictedGenre(analysis.getPredictedGenre())
                .targetAudience(analysis.getTargetAudience())
                .originalityScore(analysis.getOriginalityScore())
                .marketPotentialScore(analysis.getMarketPotentialScore())
                .predictedSuccessRate(analysis.getPredictedSuccessRate())
                .recommendations(analysis.getRecommendations())
                .build();

    }

}