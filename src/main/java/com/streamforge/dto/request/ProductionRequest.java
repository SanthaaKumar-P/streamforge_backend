package com.streamforge.dto.request;

import com.streamforge.enums.ProductionStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductionRequest {

    @NotNull(message = "Show ID is required")
    @Positive(message = "Show ID must be positive")
    private Long showId;

    @NotNull(message = "Producer ID is required")
    @Positive(message = "Producer ID must be positive")
    private Long producerId;

    @NotNull(message = "Production status is required")
    private ProductionStatus productionStatus;

    @DecimalMin(
            value = "0.0",
            message = "Allocated budget cannot be negative"
    )
    private BigDecimal allocatedBudget;

    @DecimalMin(
            value = "0.0",
            message = "Actual budget cannot be negative"
    )
    private BigDecimal actualBudget;

    private LocalDate startDate;

    private LocalDate expectedEndDate;

    private LocalDate completionDate;

    @Size(
            max = 2000,
            message = "Notes cannot exceed 2000 characters"
    )
    private String notes;
}