package com.streamforge.dto.request;

import com.streamforge.enums.ShowStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowRequest {

    @NotBlank
    private String title;

    private String description;

    private String synopsis;

    private String language;

    private String targetAudience;

    private BigDecimal estimatedBudget;

    private LocalDate expectedReleaseDate;

    private ShowStatus status;

    private Long creatorId;

}