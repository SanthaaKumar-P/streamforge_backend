package com.streamforge.serviceimpl;

import com.streamforge.dto.request.EvaluationRequest;
import com.streamforge.dto.response.EvaluationResponse;
import com.streamforge.entity.Evaluation;
import com.streamforge.entity.Show;
import com.streamforge.entity.User;
import com.streamforge.exception.ResourceNotFoundException;
import com.streamforge.mapper.EvaluationMapper;
import com.streamforge.repository.EvaluationRepository;
import com.streamforge.repository.ShowRepository;
import com.streamforge.repository.UserRepository;
import com.streamforge.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluationServiceImpl implements EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final ShowRepository showRepository;
    private final UserRepository userRepository;
    private final EvaluationMapper evaluationMapper;

    @Override
    public EvaluationResponse createEvaluation(EvaluationRequest request) {

        Show show = showRepository.findById(request.getShowId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Show not found with id: " + request.getShowId()
                        )
                );

        User evaluator = userRepository.findById(request.getEvaluatorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Evaluator not found with id: "
                                        + request.getEvaluatorId()
                        )
                );

        Evaluation evaluation = Evaluation.builder()
                .show(show)
                .evaluator(evaluator)
                .originalityScore(request.getOriginalityScore())
                .creativityScore(request.getCreativityScore())
                .marketPotentialScore(request.getMarketPotentialScore())
                .feasibilityScore(request.getFeasibilityScore())
                .overallScore(request.getOverallScore())
                .decision(request.getDecision())
                .remarks(request.getRemarks())
                .build();

        return evaluationMapper.toResponse(
                evaluationRepository.save(evaluation)
        );
    }

    @Override
    public List<EvaluationResponse> getShowEvaluations(Long showId) {

        if (!showRepository.existsById(showId)) {
            throw new ResourceNotFoundException(
                    "Show not found with id: " + showId
            );
        }

        return evaluationRepository.findByShowShowId(showId)
                .stream()
                .map(evaluationMapper::toResponse)
                .toList();
    }

    @Override
    public EvaluationResponse getEvaluationById(Long evaluationId) {

        return evaluationRepository.findById(evaluationId)
                .map(evaluationMapper::toResponse)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Evaluation not found with id: " + evaluationId
                        )
                );
    }

    @Override
    public EvaluationResponse updateEvaluation(
            Long evaluationId,
            EvaluationRequest request
    ) {

        Evaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Evaluation not found with id: " + evaluationId
                        )
                );

        evaluation.setOriginalityScore(
                request.getOriginalityScore()
        );

        evaluation.setCreativityScore(
                request.getCreativityScore()
        );

        evaluation.setMarketPotentialScore(
                request.getMarketPotentialScore()
        );

        evaluation.setFeasibilityScore(
                request.getFeasibilityScore()
        );

        evaluation.setOverallScore(
                request.getOverallScore()
        );

        evaluation.setDecision(
                request.getDecision()
        );

        evaluation.setRemarks(
                request.getRemarks()
        );

        return evaluationMapper.toResponse(
                evaluationRepository.save(evaluation)
        );
    }

    @Override
    public void deleteEvaluation(Long evaluationId) {

        Evaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Evaluation not found with id: " + evaluationId
                        )
                );

        evaluationRepository.delete(evaluation);
    }
}