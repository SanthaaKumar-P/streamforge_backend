package com.streamforge.dto.response;

import com.streamforge.enums.ProductionStatus;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductionResponse {

    private Long productionId;

    private ProductionStatus productionStatus;

    private BigDecimal allocatedBudget;

    private BigDecimal actualBudget;

    private String notes;

}