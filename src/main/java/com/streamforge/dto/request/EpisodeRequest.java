package com.streamforge.dto.request;

import com.streamforge.enums.EpisodeStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EpisodeRequest {

    @NotNull(message = "Episode number is required")
    @Positive(message = "Episode number must be positive")
    private Integer episodeNumber;

    @NotBlank(message = "Episode title is required")
    @Size(
            min = 2,
            max = 200,
            message = "Episode title must be between 2 and 200 characters"
    )
    private String title;

    @Min(
            value = 1,
            message = "Duration must be at least 1 minute"
    )
    private Integer durationMinutes;

    @Size(
            max = 2000,
            message = "Description cannot exceed 2000 characters"
    )
    private String description;

    private EpisodeStatus status;

    @NotNull(message = "Show ID is required")
    @Positive(message = "Show ID must be a positive number")
    private Long showId;
}