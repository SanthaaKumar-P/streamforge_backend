package com.streamforge.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIAnalysisResponse {

    private Long analysisId;

    private String summary;

    private String predictedGenre;

    private String targetAudience;

    private BigDecimal originalityScore;

    private BigDecimal marketPotentialScore;

    private BigDecimal predictedSuccessRate;

    private String recommendations;

}