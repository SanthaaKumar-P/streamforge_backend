package com.streamforge.dto.request;

import com.streamforge.enums.EpisodeStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EpisodeRequest {

    private Integer episodeNumber;

    @NotBlank
    private String title;

    private Integer durationMinutes;

    private String description;

    private EpisodeStatus status;

    private Long showId;

}