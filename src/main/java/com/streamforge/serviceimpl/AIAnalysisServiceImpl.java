package com.streamforge.serviceimpl;

import com.streamforge.dto.response.AIAnalysisResponse;
import com.streamforge.entity.AIAnalysis;
import com.streamforge.entity.Show;
import com.streamforge.exception.ResourceNotFoundException;
import com.streamforge.mapper.AIAnalysisMapper;
import com.streamforge.repository.AIAnalysisRepository;
import com.streamforge.repository.ShowRepository;
import com.streamforge.service.AIAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AIAnalysisServiceImpl implements AIAnalysisService {

    private final AIAnalysisRepository analysisRepository;
    private final ShowRepository showRepository;
    private final AIAnalysisMapper analysisMapper;

    @Override
    public AIAnalysisResponse getAnalysisByShow(Long showId) {

        // First verify that the show exists
        if (!showRepository.existsById(showId)) {
            throw new ResourceNotFoundException(
                    "Show not found with id: " + showId
            );
        }

        return analysisRepository.findByShowShowId(showId)
                .map(analysisMapper::toResponse)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "AI Analysis not found for show id: " + showId
                        )
                );
    }

    @Override
    public AIAnalysisResponse createAnalysis(
            Long showId,
            AIAnalysisResponse request
    ) {

        Show show = showRepository.findById(showId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Show not found with id: " + showId
                        )
                );

        AIAnalysis analysis = AIAnalysis.builder()
                .show(show)
                .summary(request.getSummary())
                .predictedGenre(request.getPredictedGenre())
                .targetAudience(request.getTargetAudience())
                .originalityScore(request.getOriginalityScore())
                .marketPotentialScore(request.getMarketPotentialScore())
                .predictedSuccessRate(request.getPredictedSuccessRate())
                .recommendations(request.getRecommendations())
                .build();

        return analysisMapper.toResponse(
                analysisRepository.save(analysis)
        );
    }
}