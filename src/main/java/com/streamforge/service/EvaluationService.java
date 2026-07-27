package com.streamforge.service;

import com.streamforge.dto.request.EvaluationRequest;
import com.streamforge.dto.response.EvaluationResponse;

import java.util.List;

public interface EvaluationService {

    EvaluationResponse createEvaluation(EvaluationRequest request);

    List<EvaluationResponse> getShowEvaluations(Long showId);

    EvaluationResponse getEvaluationById(Long evaluationId);

    EvaluationResponse updateEvaluation(Long evaluationId, EvaluationRequest request);

    void deleteEvaluation(Long evaluationId);

}