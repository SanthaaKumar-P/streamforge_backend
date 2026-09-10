package com.streamforge.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIFuturePlannerResponse {

    private Long showId;

    private String showTitle;

    private BigDecimal currentDemandScore;

    private BigDecimal projectedDemandScore;

    private BigDecimal priceChangePercent;

    private String priceOutlook;

    private String peakMonth;

    private BigDecimal confidenceScore;

    private String recommendation;

    private List<ForecastPoint> forecast;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ForecastPoint {

        private String month;

        private BigDecimal demandScore;

        private BigDecimal valueScore;

        private BigDecimal priceChangePercent;

        private BigDecimal seasonalityIndex;

    }
}