package com.streamforge.serviceimpl;

import com.streamforge.dto.response.AIFuturePlannerResponse;
import com.streamforge.entity.AIAnalysis;
import com.streamforge.entity.Show;
import com.streamforge.exception.ResourceNotFoundException;
import com.streamforge.repository.AIAnalysisRepository;
import com.streamforge.repository.ShowRepository;
import com.streamforge.service.AIFuturePlannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AIFuturePlannerServiceImpl
        implements AIFuturePlannerService {

    private final ShowRepository showRepository;

    private final AIAnalysisRepository aiAnalysisRepository;


    /* =========================================================
       GENERATE FUTURE FORECAST
    ========================================================= */

    @Override
    public AIFuturePlannerResponse generateForecast(
            Long showId
    ) {

        Show show =
                showRepository.findById(showId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Show not found with id: "
                                                + showId
                                )
                        );


        /*
         * Existing AI analysis is used when available.
         *
         * This means the Future Planner builds on the
         * previous AI Prediction instead of being an
         * unrelated calculation.
         */

        AIAnalysis aiAnalysis =
                aiAnalysisRepository
                        .findByShowShowId(showId)
                        .orElse(null);


        double baseDemand =
                calculateBaseDemand(
                        show,
                        aiAnalysis
                );


        double baseValue =
                calculateBaseValue(
                        show,
                        aiAnalysis
                );


        double confidence =
                calculateConfidence(
                        show,
                        aiAnalysis
                );


        /*
         * Generate the next six months.
         */

        List<AIFuturePlannerResponse.ForecastPoint>
                forecast =
                new ArrayList<>();


        LocalDate currentDate =
                LocalDate.now();


        double previousDemand =
                baseDemand;


        double previousValue =
                baseValue;


        String peakMonth =
                "";


        double peakDemand =
                0;


        for (
                int i = 1;
                i <= 6;
                i++
        ) {

            LocalDate forecastDate =
                    currentDate.plusMonths(i);


            double seasonality =
                    calculateSeasonality(
                            forecastDate
                    );


            double demand =
                    calculateFutureDemand(
                            baseDemand,
                            seasonality,
                            i,
                            show
                    );


            double value =
                    calculateFutureValue(
                            baseValue,
                            demand,
                            seasonality,
                            i
                    );


            double priceChange =
                    calculatePriceChange(
                            baseValue,
                            value
                    );


            if (
                    demand > peakDemand
            ) {

                peakDemand =
                        demand;

                peakMonth =
                        forecastDate
                                .getMonth()
                                .getDisplayName(
                                        TextStyle.SHORT,
                                        Locale.ENGLISH
                                )
                                + " "
                                + forecastDate.getYear();
            }


            forecast.add(
                    AIFuturePlannerResponse
                            .ForecastPoint
                            .builder()
                            .month(
                                    forecastDate
                                            .getMonth()
                                            .getDisplayName(
                                                    TextStyle.SHORT,
                                                    Locale.ENGLISH
                                            )
                                            + " "
                                            + forecastDate.getYear()
                            )
                            .demandScore(
                                    score(demand)
                            )
                            .valueScore(
                                    score(value)
                            )
                            .priceChangePercent(
                                    score(
                                            priceChange
                                    )
                            )
                            .seasonalityIndex(
                                    score(
                                            seasonality
                                    )
                            )
                            .build()
            );


            previousDemand =
                    demand;

            previousValue =
                    value;
        }


        double projectedDemand =
                forecast.isEmpty()
                        ? baseDemand
                        : forecast
                        .get(
                                forecast.size() - 1
                        )
                        .getDemandScore()
                        .doubleValue();


        double finalValue =
                forecast.isEmpty()
                        ? baseValue
                        : forecast
                        .get(
                                forecast.size() - 1
                        )
                        .getValueScore()
                        .doubleValue();


        double priceChange =
                calculatePriceChange(
                        baseValue,
                        finalValue
                );


        String priceOutlook =
                getPriceOutlook(
                        priceChange
                );


        String recommendation =
                buildRecommendation(
                        show,
                        baseDemand,
                        projectedDemand,
                        priceChange,
                        peakMonth,
                        confidence
                );


        return AIFuturePlannerResponse
                .builder()
                .showId(
                        show.getShowId()
                )
                .showTitle(
                        show.getTitle()
                )
                .currentDemandScore(
                        score(baseDemand)
                )
                .projectedDemandScore(
                        score(projectedDemand)
                )
                .priceChangePercent(
                        score(priceChange)
                )
                .priceOutlook(
                        priceOutlook
                )
                .peakMonth(
                        peakMonth
                )
                .confidenceScore(
                        score(confidence)
                )
                .recommendation(
                        recommendation
                )
                .forecast(
                        forecast
                )
                .build();
    }


    /* =========================================================
       BASE DEMAND
    ========================================================= */

    private double calculateBaseDemand(
            Show show,
            AIAnalysis analysis
    ) {

        double demand = 50.0;


        /*
         * AI market potential
         */

        if (
                analysis != null &&
                analysis.getMarketPotentialScore()
                        != null
        ) {

            demand =
                    analysis
                            .getMarketPotentialScore()
                            .doubleValue();

        } else {

            /*
             * No previous AI analysis.
             * Build a baseline from available show data.
             */

            if (
                    show.getTargetAudience()
                            != null &&
                    !show.getTargetAudience()
                            .isBlank()
            ) {
                demand += 10;
            }


            if (
                    show.getLanguage()
                            != null &&
                    !show.getLanguage()
                            .isBlank()
            ) {
                demand += 5;
            }


            if (
                    show.getSynopsis()
                            != null &&
                    show.getSynopsis()
                            .length() >= 150
            ) {
                demand += 8;
            }


            if (
                    show.getDescription()
                            != null &&
                    show.getDescription()
                            .length() >= 200
            ) {
                demand += 7;
            }
        }


        /*
         * Audience adjustment
         */

        String audience =
                safe(
                        show.getTargetAudience()
                ).toLowerCase();


        if (
                audience.contains("young") ||
                audience.contains("adult") ||
                audience.contains("family")
        ) {

            demand += 5;
        }


        /*
         * Release date availability
         */

        if (
                show.getExpectedReleaseDate()
                        != null
        ) {

            demand += 4;
        }


        return clamp(demand);
    }


    /* =========================================================
       BASE VALUE
    ========================================================= */

    private double calculateBaseValue(
            Show show,
            AIAnalysis analysis
    ) {

        double value = 50.0;


        /*
         * Success probability has the strongest
         * influence on future content value.
         */

        if (
                analysis != null &&
                analysis.getPredictedSuccessRate()
                        != null
        ) {

            value =
                    analysis
                            .getPredictedSuccessRate()
                            .doubleValue();

        } else {

            if (
                    show.getEstimatedBudget()
                            != null &&
                    show.getEstimatedBudget()
                            .doubleValue() > 0
            ) {

                value += 10;
            }


            if (
                    show.getSynopsis()
                            != null &&
                    show.getSynopsis()
                            .length() >= 150
            ) {

                value += 8;
            }


            if (
                    show.getTargetAudience()
                            != null &&
                    !show.getTargetAudience()
                            .isBlank()
            ) {

                value += 7;
            }
        }


        return clamp(value);
    }


    /* =========================================================
       CONFIDENCE
    ========================================================= */

    private double calculateConfidence(
            Show show,
            AIAnalysis analysis
    ) {

        double confidence = 55.0;


        /*
         * Existing AI analysis makes the forecast
         * more grounded.
         */

        if (
                analysis != null
        ) {

            confidence += 15;
        }


        if (
                show.getSynopsis() != null &&
                show.getSynopsis().length() >= 150
        ) {

            confidence += 8;
        }


        if (
                show.getDescription() != null &&
                show.getDescription().length() >= 200
        ) {

            confidence += 5;
        }


        if (
                show.getTargetAudience() != null &&
                !show.getTargetAudience().isBlank()
        ) {

            confidence += 5;
        }


        if (
                show.getExpectedReleaseDate() != null
        ) {

            confidence += 5;
        }


        if (
                show.getEstimatedBudget() != null
        ) {

            confidence += 4;
        }


        return clamp(confidence);
    }


    /* =========================================================
       SEASONALITY
    ========================================================= */

    private double calculateSeasonality(
            LocalDate date
    ) {

        int month =
                date.getMonthValue();


        /*
         * Demo seasonality model.
         *
         * Higher audience activity around:
         *
         * October
         * November
         * December
         *
         * This is a planning heuristic, not
         * historical Netflix data.
         */

        switch (month) {

            case 10:
                return 78;

            case 11:
                return 86;

            case 12:
                return 94;

            case 1:
                return 82;

            case 2:
                return 74;

            case 3:
                return 70;

            case 4:
                return 72;

            case 5:
                return 75;

            case 6:
                return 77;

            case 7:
                return 80;

            case 8:
                return 76;

            case 9:
                return 73;

            default:
                return 70;
        }
    }


    /* =========================================================
       FUTURE DEMAND
    ========================================================= */

    private double calculateFutureDemand(
            double baseDemand,
            double seasonality,
            int monthNumber,
            Show show
    ) {

        /*
         * Gradual growth factor.
         */

        double growth =
                monthNumber * 1.5;


        /*
         * Seasonality influence.
         */

        double seasonalInfluence =
                (
                        seasonality - 70
                ) * 0.22;


        /*
         * Production readiness.
         */

        double productionBonus =
                0;


        if (
                show.getStatus() != null
        ) {

            String status =
                    show.getStatus()
                            .name()
                            .toUpperCase();


            if (
                    status.equals(
                            "APPROVED"
                    )
            ) {

                productionBonus = 3;

            } else if (
                    status.equals(
                            "IN_PRODUCTION"
                    )
            ) {

                productionBonus = 6;

            } else if (
                    status.equals(
                            "COMPLETED"
                    )
            ) {

                productionBonus = 8;
            }
        }


        return clamp(
                baseDemand
                        + growth
                        + seasonalInfluence
                        + productionBonus
        );
    }


    /* =========================================================
       FUTURE VALUE
    ========================================================= */

    private double calculateFutureValue(
            double baseValue,
            double demand,
            double seasonality,
            int monthNumber
    ) {

        double demandInfluence =
                (
                        demand - 50
                ) * 0.18;


        double seasonalityInfluence =
                (
                        seasonality - 70
                ) * 0.10;


        double timeGrowth =
                monthNumber * 0.8;


        return clamp(
                baseValue
                        + demandInfluence
                        + seasonalityInfluence
                        + timeGrowth
        );
    }


    /* =========================================================
       PRICE / VALUE CHANGE
    ========================================================= */

    private double calculatePriceChange(
            double baseValue,
            double futureValue
    ) {

        if (
                baseValue <= 0
        ) {

            return 0;
        }


        return (
                (
                        futureValue -
                                baseValue
                )
                        / baseValue
        ) * 100;
    }


    /* =========================================================
       PRICE OUTLOOK
    ========================================================= */

    private String getPriceOutlook(
            double priceChange
    ) {

        if (
                priceChange >= 10
        ) {

            return "Strongly Increasing";

        } else if (
                priceChange >= 4
        ) {

            return "Increasing";

        } else if (
                priceChange <= -5
        ) {

            return "Declining";

        } else {

            return "Stable";
        }
    }


    /* =========================================================
       RECOMMENDATION
    ========================================================= */

    private String buildRecommendation(
            Show show,
            double currentDemand,
            double projectedDemand,
            double priceChange,
            String peakMonth,
            double confidence
    ) {

        StringBuilder recommendation =
                new StringBuilder();


        if (
                projectedDemand >
                        currentDemand + 8
        ) {

            recommendation.append(
                    "Demand is expected to strengthen "
                            + "over the forecast period. "
            );

        } else if (
                projectedDemand >
                        currentDemand
        ) {

            recommendation.append(
                    "Demand shows a positive but moderate "
                            + "future trend. "
            );

        } else {

            recommendation.append(
                    "Demand is expected to remain relatively "
                            + "stable; focus on audience positioning. "
            );
        }


        if (
                priceChange >= 8
        ) {

            recommendation.append(
                    "Content value is projected to increase, "
                            + "so production readiness should be prioritized. "
            );

        } else if (
                priceChange >= 3
        ) {

            recommendation.append(
                    "Content value shows a positive outlook; "
                            + "maintain the planned production timeline. "
            );

        } else {

            recommendation.append(
                    "Maintain cost discipline while improving "
                            + "audience positioning. "
            );
        }


        if (
                peakMonth != null &&
                !peakMonth.isBlank()
        ) {

            recommendation.append(
                    "The projected peak demand period is "
                            + peakMonth
                            + ". "
            );
        }


        if (
                show.getExpectedReleaseDate() ==
                        null
        ) {

            recommendation.append(
                    "Adding an expected release date will "
                            + "improve future planning confidence. "
            );
        }


        recommendation.append(
                "Forecast confidence is approximately "
                        + String.format(
                                Locale.US,
                                "%.0f",
                                confidence
                        )
                        + "%."
        );


        return recommendation
                .toString()
                .trim();
    }


    /* =========================================================
       SAFE STRING
    ========================================================= */

    private String safe(
            String value
    ) {

        return value == null
                ? ""
                : value.trim();
    }


    /* =========================================================
       SCORE
    ========================================================= */

    private BigDecimal score(
            double value
    ) {

        return BigDecimal
                .valueOf(
                        clamp(value)
                )
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                );
    }


    /* =========================================================
       CLAMP
    ========================================================= */

    private double clamp(
            double value
    ) {

        return Math.max(
                0,
                Math.min(
                        100,
                        value
                )
        );
    }
}