package com.streamforge.mapper;

import com.streamforge.dto.response.ProductionResponse;
import com.streamforge.entity.Production;
import org.springframework.stereotype.Component;

@Component
public class ProductionMapper {

    public ProductionResponse toResponse(Production production) {

        return ProductionResponse.builder()
                .productionId(production.getProductionId())

                .showId(
                        production.getShow() != null
                                ? production.getShow().getShowId()
                                : null
                )

                .producerId(
                        production.getProducer() != null
                                ? production.getProducer().getUserId()
                                : null
                )

                .productionStatus(
                        production.getProductionStatus()
                )

                .allocatedBudget(
                        production.getAllocatedBudget()
                )

                .actualBudget(
                        production.getActualBudget()
                )

                .startDate(
                        production.getStartDate()
                )

                .expectedEndDate(
                        production.getExpectedEndDate()
                )

                .completionDate(
                        production.getCompletionDate()
                )

                .notes(
                        production.getNotes()
                )

                .build();
    }
}