package com.streamforge.dto.response;

import com.streamforge.enums.ProductionStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductionResponse {

    private Long productionId;

    private Long showId;

    private Long producerId;

    private ProductionStatus productionStatus;

    private BigDecimal allocatedBudget;

    private BigDecimal actualBudget;

    private LocalDate startDate;

    private LocalDate expectedEndDate;

    private LocalDate completionDate;

    private String notes;
}