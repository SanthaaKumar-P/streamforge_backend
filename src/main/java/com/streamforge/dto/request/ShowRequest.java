package com.streamforge.dto.request;

import com.streamforge.enums.ShowStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowRequest {

    @NotBlank(message = "Show title is required")
    @Size(
            min = 2,
            max = 200,
            message = "Show title must be between 2 and 200 characters"
    )
    @Pattern(
            regexp = "^[\\p{L}\\p{N}][\\p{L}\\p{N} .,'!?:&()\\-]*$",
            message = "Show title contains invalid characters"
    )
    private String title;

    @Size(
            max = 2000,
            message = "Description cannot exceed 2000 characters"
    )
    private String description;

    @Size(
            max = 2000,
            message = "Synopsis cannot exceed 2000 characters"
    )
    private String synopsis;

    @Size(
            max = 50,
            message = "Language cannot exceed 50 characters"
    )
    private String language;

    @NotBlank(message = "Target audience is required")
    @Size(
            max = 100,
            message = "Target audience cannot exceed 100 characters"
    )
    private String targetAudience;

    @NotNull(message = "Episode count is required")
    @Positive(message = "Episode count must be greater than zero")
    private Integer episodeCount;

    @NotNull(message = "At least one genre is required")
    @Size(
            min = 1,
            message = "At least one genre is required"
    )
    private List<Long> genreIds;

    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "Estimated budget cannot be negative"
    )
    private BigDecimal estimatedBudget;

    @FutureOrPresent(
            message = "Expected release date cannot be in the past"
    )
    private LocalDate expectedReleaseDate;

    /*
     * Kept for backward compatibility with the current frontend.
     *
     * IMPORTANT:
     * The backend decides the initial status during CREATE.
     */
    private ShowStatus status;

    @NotNull(message = "Creator ID is required")
    @Positive(message = "Creator ID must be a positive number")
    private Long creatorId;
}