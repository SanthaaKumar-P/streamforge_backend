package com.streamforge.dto.request;

import com.streamforge.enums.EvaluationDecision;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationRequest {

    private Long showId;

    private Long evaluatorId;

    private Integer originalityScore;

    private Integer creativityScore;

    private Integer marketPotentialScore;

    private Integer feasibilityScore;

    private BigDecimal overallScore;

    private EvaluationDecision decision;

    private String remarks;

}