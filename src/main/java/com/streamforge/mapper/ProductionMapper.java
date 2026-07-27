package com.streamforge.mapper;

import com.streamforge.dto.response.ProductionResponse;
import com.streamforge.entity.Production;
import org.springframework.stereotype.Component;

@Component
public class ProductionMapper {

    public ProductionResponse toResponse(Production production){

        return ProductionResponse.builder()
                .productionId(production.getProductionId())
                .productionStatus(production.getProductionStatus())
                .allocatedBudget(production.getAllocatedBudget())
                .actualBudget(production.getActualBudget())
                .notes(production.getNotes())
                .build();

    }

}