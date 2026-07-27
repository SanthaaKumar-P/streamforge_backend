package com.streamforge.dto.response;

import com.streamforge.enums.EvaluationDecision;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationResponse {

    private Long evaluationId;

    private Integer originalityScore;

    private Integer creativityScore;

    private Integer marketPotentialScore;

    private Integer feasibilityScore;

    private BigDecimal overallScore;

    private EvaluationDecision decision;

    private String remarks;

}