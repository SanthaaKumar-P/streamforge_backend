package com.streamforge.serviceimpl;

import com.streamforge.dto.response.AIAnalysisResponse;
import com.streamforge.entity.AIAnalysis;
import com.streamforge.entity.Show;
import com.streamforge.exception.ResourceNotFoundException;
import com.streamforge.mapper.AIAnalysisMapper;
import com.streamforge.repository.AIAnalysisRepository;
import com.streamforge.repository.ShowRepository;
import com.streamforge.service.AIAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Transactional
public class AIAnalysisServiceImpl
        implements AIAnalysisService {

    private final AIAnalysisRepository analysisRepository;

    private final ShowRepository showRepository;

    private final AIAnalysisMapper analysisMapper;


    /* =========================================================
       GET EXISTING AI ANALYSIS
    ========================================================= */

    @Override
    @Transactional(readOnly = true)
    public AIAnalysisResponse getAnalysisByShow(
            Long showId
    ) {

        if (
                !showRepository.existsById(showId)
        ) {
            throw new ResourceNotFoundException(
                    "Show not found with id: " + showId
            );
        }

        return analysisRepository
                .findByShowShowId(showId)
                .map(analysisMapper::toResponse)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "AI Analysis not found for show id: "
                                        + showId
                        )
                );
    }


    /* =========================================================
       MANUAL AI ANALYSIS CREATION
    ========================================================= */

    @Override
    public AIAnalysisResponse createAnalysis(
            Long showId,
            AIAnalysisResponse request
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
         * Reuse the existing analysis if one exists.
         *
         * This avoids creating multiple AI analysis
         * records for the same show.
         */

        AIAnalysis analysis =
                analysisRepository
                        .findByShowShowId(showId)
                        .orElseGet(
                                AIAnalysis::new
                        );


        analysis.setShow(show);

        analysis.setSummary(
                request.getSummary()
        );

        analysis.setPredictedGenre(
                request.getPredictedGenre()
        );

        analysis.setTargetAudience(
                request.getTargetAudience()
        );

        analysis.setOriginalityScore(
                request.getOriginalityScore()
        );

        analysis.setMarketPotentialScore(
                request.getMarketPotentialScore()
        );

        analysis.setPredictedSuccessRate(
                request.getPredictedSuccessRate()
        );

        analysis.setRecommendations(
                request.getRecommendations()
        );


        AIAnalysis saved =
                analysisRepository.save(
                        analysis
                );


        return analysisMapper.toResponse(
                saved
        );
    }


    /* =========================================================
       AI PREDICTION
    ========================================================= */

    @Override
    public AIAnalysisResponse predictAnalysis(
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
         * -----------------------------------------------------
         * AI-ASSISTED PREDICTIVE SCORING
         * -----------------------------------------------------
         *
         * This is a lightweight prediction engine for the
         * project demo.
         *
         * It uses:
         *
         * 1. Title
         * 2. Synopsis
         * 3. Description
         * 4. Target audience
         * 5. Language
         * 6. Budget
         * 7. Release timeline
         * 8. Current workflow status
         *
         * No external AI API is required.
         * No Python service is required.
         */

        String title =
                safe(show.getTitle());

        String synopsis =
                safe(show.getSynopsis());

        String description =
                safe(show.getDescription());

        String targetAudience =
                safe(show.getTargetAudience());

        String language =
                safe(show.getLanguage());


        BigDecimal budget =
                show.getEstimatedBudget();


        LocalDate releaseDate =
                show.getExpectedReleaseDate();


        /*
         * Predict genre using textual signals.
         *
         * We deliberately DO NOT use ShowGenre here,
         * because ShowGenre.getGenre() returns a Genre
         * object and would introduce unnecessary type
         * coupling.
         */

        String predictedGenre =
                predictGenre(
                        title,
                        synopsis,
                        description
                );


        /*
         * Target audience
         */

        String predictedAudience =
                targetAudience.isBlank()
                        ? "General Audience"
                        : targetAudience;


        /*
         * Calculate scores
         */

        double originality =
                calculateOriginality(
                        title,
                        synopsis,
                        description
                );


        double marketPotential =
                calculateMarketPotential(
                        targetAudience,
                        language,
                        budget,
                        releaseDate,
                        synopsis
                );


        double successRate =
                calculateSuccessRate(
                        originality,
                        marketPotential,
                        show
                );


        /*
         * Generate explanation
         */

        String summary =
                buildSummary(
                        show,
                        predictedGenre,
                        predictedAudience,
                        originality,
                        marketPotential,
                        successRate
                );


        /*
         * Generate recommendations
         */

        String recommendations =
                buildRecommendations(
                        show,
                        originality,
                        marketPotential,
                        successRate
                );


        /*
         * Reuse existing analysis if available.
         */

        AIAnalysis analysis =
                analysisRepository
                        .findByShowShowId(showId)
                        .orElseGet(
                                AIAnalysis::new
                        );


        analysis.setShow(show);

        analysis.setSummary(
                summary
        );

        analysis.setPredictedGenre(
                predictedGenre
        );

        analysis.setTargetAudience(
                predictedAudience
        );

        analysis.setOriginalityScore(
                toScore(originality)
        );

        analysis.setMarketPotentialScore(
                toScore(marketPotential)
        );

        analysis.setPredictedSuccessRate(
                toScore(successRate)
        );

        analysis.setRecommendations(
                recommendations
        );


        AIAnalysis saved =
                analysisRepository.save(
                        analysis
                );


        return analysisMapper.toResponse(
                saved
        );
    }


    /* =========================================================
       GENRE PREDICTION
    ========================================================= */

    private String predictGenre(
            String title,
            String synopsis,
            String description
    ) {

        String text =
                (
                        title
                                + " "
                                + synopsis
                                + " "
                                + description
                )
                        .toLowerCase();


        if (
                containsAny(
                        text,
                        "crime",
                        "murder",
                        "detective",
                        "killer",
                        "criminal",
                        "police",
                        "investigation"
                )
        ) {

            return "Crime / Thriller";
        }


        if (
                containsAny(
                        text,
                        "love",
                        "romance",
                        "relationship",
                        "wedding",
                        "heart"
                )
        ) {

            return "Romance";
        }


        if (
                containsAny(
                        text,
                        "space",
                        "future",
                        "robot",
                        "technology",
                        "artificial intelligence",
                        "ai",
                        "planet"
                )
        ) {

            return "Science Fiction";
        }


        if (
                containsAny(
                        text,
                        "comedy",
                        "funny",
                        "humor",
                        "laugh"
                )
        ) {

            return "Comedy";
        }


        if (
                containsAny(
                        text,
                        "horror",
                        "ghost",
                        "haunted",
                        "supernatural",
                        "evil"
                )
        ) {

            return "Horror";
        }


        if (
                containsAny(
                        text,
                        "family",
                        "school",
                        "friendship",
                        "life",
                        "journey"
                )
        ) {

            return "Drama";
        }


        return "Drama";
    }


    /* =========================================================
       ORIGINALITY SCORE
    ========================================================= */

    private double calculateOriginality(
            String title,
            String synopsis,
            String description
    ) {

        double score = 55.0;


        /*
         * Rich synopsis indicates stronger
         * concept definition.
         */

        if (
                synopsis.length() >= 100
        ) {
            score += 8;
        }


        if (
                synopsis.length() >= 250
        ) {
            score += 7;
        }


        /*
         * Detailed description
         */

        if (
                description.length() >= 150
        ) {
            score += 5;
        }


        if (
                description.length() >= 400
        ) {
            score += 5;
        }


        /*
         * Reasonable title length
         */

        if (
                title.length() >= 5 &&
                title.length() <= 60
        ) {
            score += 5;
        }


        /*
         * Creative concept indicators
         */

        String combined =
                (
                        title
                                + " "
                                + synopsis
                                + " "
                                + description
                )
                        .toLowerCase();


        String[] creativeSignals = {

                "unexpected",
                "unique",
                "mystery",
                "twist",
                "survival",
                "parallel",
                "future",
                "secret",
                "journey",
                "identity",
                "conspiracy",
                "legacy",
                "unknown",
                "hidden"

        };


        for (
                String signal :
                creativeSignals
        ) {

            if (
                    combined.contains(
                            signal
                    )
            ) {

                score += 2;
            }
        }


        return clamp(score);
    }


    /* =========================================================
       MARKET POTENTIAL
    ========================================================= */

    private double calculateMarketPotential(
            String targetAudience,
            String language,
            BigDecimal budget,
            LocalDate releaseDate,
            String synopsis
    ) {

        double score = 50.0;


        /*
         * Defined target audience
         */

        if (
                !targetAudience.isBlank()
        ) {

            score += 10;
        }


        /*
         * Language information
         */

        if (
                !language.isBlank()
        ) {

            score += 5;
        }


        /*
         * Broad audience categories
         */

        String audience =
                targetAudience.toLowerCase();


        if (
                audience.contains("young") ||
                audience.contains("adult") ||
                audience.contains("family")
        ) {

            score += 8;
        }


        /*
         * Budget
         */

        if (
                budget != null &&
                budget.compareTo(
                        BigDecimal.ZERO
                ) > 0
        ) {

            if (
                    budget.compareTo(
                            new BigDecimal(
                                    "1000000"
                            )
                    ) >= 0
            ) {

                score += 8;

            } else {

                score += 4;
            }
        }


        /*
         * Release timeline
         */

        if (
                releaseDate != null
        ) {

            long days =
                    ChronoUnit.DAYS.between(
                            LocalDate.now(),
                            releaseDate
                    );


            if (
                    days >= 0 &&
                    days <= 365
            ) {

                score += 7;
            }
        }


        /*
         * Detailed synopsis
         */

        if (
                synopsis.length() >= 150
        ) {

            score += 5;
        }


        return clamp(score);
    }


    /* =========================================================
       SUCCESS RATE
    ========================================================= */

    private double calculateSuccessRate(
            double originality,
            double marketPotential,
            Show show
    ) {

        /*
         * Weighted prediction:
         *
         * Originality       = 45%
         * Market Potential  = 55%
         */

        double score =
                (
                        originality * 0.45
                                + marketPotential * 0.55
                );


        /*
         * Workflow maturity adjustment
         */

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

                score += 5;

            } else if (
                    status.equals(
                            "IN_PRODUCTION"
                    )
            ) {

                score += 7;

            } else if (
                    status.equals(
                            "UNDER_REVIEW"
                    )
            ) {

                score += 2;

            } else if (
                    status.equals(
                            "REJECTED"
                    )
            ) {

                score -= 8;
            }
        }


        return clamp(score);
    }


    /* =========================================================
       SUMMARY
    ========================================================= */

    private String buildSummary(
            Show show,
            String predictedGenre,
            String predictedAudience,
            double originality,
            double marketPotential,
            double successRate
    ) {

        return String.format(
                "AI analysis indicates that \"%s\" "
                        + "has a predicted genre of %s "
                        + "and is positioned for %s. "
                        + "The concept scores %.0f/100 "
                        + "for originality and %.0f/100 "
                        + "for market potential, resulting "
                        + "in an estimated success probability "
                        + "of %.0f%%.",
                safe(show.getTitle()),
                predictedGenre,
                predictedAudience,
                originality,
                marketPotential,
                successRate
        );
    }


    /* =========================================================
       RECOMMENDATIONS
    ========================================================= */

    private String buildRecommendations(
            Show show,
            double originality,
            double marketPotential,
            double successRate
    ) {

        StringBuilder recommendations =
                new StringBuilder();


        /*
         * Originality
         */

        if (
                originality < 60
        ) {

            recommendations.append(
                    "Strengthen the unique story hook "
                            + "and character differentiation. "
            );

        } else if (
                originality >= 80
        ) {

            recommendations.append(
                    "The concept has a strong originality "
                            + "signal; preserve its distinctive "
                            + "story identity. "
            );
        }


        /*
         * Market potential
         */

        if (
                marketPotential < 60
        ) {

            recommendations.append(
                    "Refine the target audience and "
                            + "market positioning strategy. "
            );

        } else if (
                marketPotential >= 80
        ) {

            recommendations.append(
                    "Market positioning looks promising; "
                            + "prioritize audience-focused promotion. "
            );
        }


        /*
         * Release date
         */

        if (
                show.getExpectedReleaseDate() ==
                        null
        ) {

            recommendations.append(
                    "Add an expected release date "
                            + "for better timeline forecasting. "
            );
        }


        /*
         * Budget
         */

        if (
                show.getEstimatedBudget() ==
                        null
        ) {

            recommendations.append(
                    "Add an estimated production budget "
                            + "for stronger financial forecasting. "
            );
        }


        /*
         * Final recommendation
         */

        if (
                successRate >= 80
        ) {

            recommendations.append(
                    "Overall prediction is strong; "
                            + "the project is suitable for "
                            + "deeper evaluation and production planning."
            );

        } else if (
                successRate >= 65
        ) {

            recommendations.append(
                    "The project shows moderate-to-strong "
                            + "potential; address the identified "
                            + "gaps before final approval."
            );

        } else {

            recommendations.append(
                    "The project requires further refinement "
                            + "before progressing to final approval."
            );
        }


        return recommendations
                .toString()
                .trim();
    }


    /* =========================================================
       STRING HELPER
    ========================================================= */

    private boolean containsAny(
            String text,
            String... values
    ) {

        for (
                String value :
                values
        ) {

            if (
                    text.contains(
                            value
                    )
            ) {

                return true;
            }
        }

        return false;
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
       SCORE CONVERSION
    ========================================================= */

    private BigDecimal toScore(
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