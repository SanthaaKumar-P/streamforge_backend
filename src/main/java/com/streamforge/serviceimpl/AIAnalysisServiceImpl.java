package com.streamforge.serviceimpl;

import com.streamforge.dto.response.AIAnalysisResponse;
import com.streamforge.entity.AIAnalysis;
import com.streamforge.entity.Show;
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

        return analysisRepository.findByShowShowId(showId)
                .map(analysisMapper::toResponse)
                .orElseThrow(
                        () -> new RuntimeException("AI Analysis not found")
                );

    }


    @Override
    public AIAnalysisResponse createAnalysis(
            Long showId,
            AIAnalysisResponse request
    ) {


        Show show = showRepository.findById(showId)
                .orElseThrow(
                        () -> new RuntimeException("Show not found")
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