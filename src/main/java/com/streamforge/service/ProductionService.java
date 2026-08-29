package com.streamforge.service;

import com.streamforge.dto.request.ProductionRequest;
import com.streamforge.dto.response.ProductionResponse;

import java.util.List;

public interface ProductionService {

    ProductionResponse createProduction(
            ProductionRequest request
    );

    ProductionResponse getProductionById(
            Long productionId
    );

    List<ProductionResponse> getProductionsByShow(
            Long showId
    );

    List<ProductionResponse> getAllProductions();

    ProductionResponse updateProduction(
            Long productionId,
            ProductionRequest request
    );

    void deleteProduction(
            Long productionId
    );
}