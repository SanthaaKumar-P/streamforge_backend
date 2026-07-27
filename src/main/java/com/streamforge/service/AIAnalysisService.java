package com.streamforge.service;

import com.streamforge.dto.response.AIAnalysisResponse;

public interface AIAnalysisService {

    AIAnalysisResponse getAnalysisByShow(Long showId);

    AIAnalysisResponse createAnalysis(
            Long showId,
            AIAnalysisResponse request
    );

}