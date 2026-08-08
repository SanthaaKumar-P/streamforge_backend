package com.streamforge.dto.request;

import com.streamforge.enums.EvaluationDecision;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationRequest {

    @NotNull(message = "Show ID is required")
    @Positive(message = "Show ID must be positive")
    private Long showId;

    @NotNull(message = "Evaluator ID is required")
    @Positive(message = "Evaluator ID must be positive")
    private Long evaluatorId;

    @NotNull(message = "Originality score is required")
    @DecimalMin(
            value = "0",
            message = "Originality score cannot be less than 0"
    )
    @DecimalMax(
            value = "10",
            message = "Originality score cannot be greater than 10"
    )
    private Integer originalityScore;

    @NotNull(message = "Creativity score is required")
    @DecimalMin(
            value = "0",
            message = "Creativity score cannot be less than 0"
    )
    @DecimalMax(
            value = "10",
            message = "Creativity score cannot be greater than 10"
    )
    private Integer creativityScore;

    @NotNull(message = "Market potential score is required")
    @DecimalMin(
            value = "0",
            message = "Market potential score cannot be less than 0"
    )
    @DecimalMax(
            value = "10",
            message = "Market potential score cannot be greater than 10"
    )
    private Integer marketPotentialScore;

    @NotNull(message = "Feasibility score is required")
    @DecimalMin(
            value = "0",
            message = "Feasibility score cannot be less than 0"
    )
    @DecimalMax(
            value = "10",
            message = "Feasibility score cannot be greater than 10"
    )
    private Integer feasibilityScore;

    @NotNull(message = "Overall score is required")
    @DecimalMin(
            value = "0",
            message = "Overall score cannot be less than 0"
    )
    @DecimalMax(
            value = "10",
            message = "Overall score cannot be greater than 10"
    )
    private BigDecimal overallScore;

    @NotNull(message = "Evaluation decision is required")
    private EvaluationDecision decision;

    @Size(
            max = 2000,
            message = "Remarks cannot exceed 2000 characters"
    )
    private String remarks;
}