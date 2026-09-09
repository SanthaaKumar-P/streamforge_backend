package com.streamforge.dto.response;

import com.streamforge.enums.ShowStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowResponse {

    private Long showId;

    private String title;

    private String description;

    private String synopsis;

    private String language;

    private String targetAudience;

    private Integer episodeCount;

    private BigDecimal estimatedBudget;

    private LocalDate expectedReleaseDate;

    private ShowStatus status;

    private UserResponse creator;

    private List<GenreResponse> genres;
}